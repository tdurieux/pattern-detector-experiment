/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Address;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Route;
import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.RouteDatabase;
import com.squareup.okhttp.internal.io.RealConnection;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import okio.Sink;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 */
public final class StreamAllocation {
  public final Address address;
  private final ConnectionPool connectionPool;

  // State guarded by this.
  private RouteSelector routeSelector;
  private RealConnection connection;
  private boolean released;
  private boolean canceled;
  private HttpStream stream;

  public StreamAllocation(ConnectionPool connectionPool, Address address) {
    this.connectionPool = connectionPool;
    this.address = address;
  }

  public HttpStream newStream(int connectTimeout, int readTimeout, int writeTimeout,
      boolean connectionRetryEnabled, boolean doExtensiveHealthChecks)
      throws RouteException, IOException {
    try {
      RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout,
          writeTimeout, connectionRetryEnabled, doExtensiveHealthChecks);

      HttpStream resultStream;
      if (resultConnection.framedConnection != null) {
        resultStream = new Http2xStream(resultConnection.framedConnection);
      } else {
        resultConnection.getSocket().setSoTimeout(readTimeout);
        resultConnection.source.timeout().timeout(readTimeout, MILLISECONDS);
        resultConnection.sink.timeout().timeout(writeTimeout, MILLISECONDS);
        resultStream = new Http1xStream(this, resultConnection.source, resultConnection.sink);
      }

      synchronized (connectionPool) {
        resultConnection.streamCount++;
        stream = resultStream;
        return resultStream;
      }
    } catch (IOException e) {
      throw new RouteException(e);
    }
  }

  /**
   * Finds a connection and returns it if it is healthy. If it is unhealthy the process is repeated
   * until a healthy connection is found.
   */
  private RealConnection findHealthyConnection(int connectTimeout, int readTimeout,
      int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks)
      throws IOException, RouteException {
    while (true) {
      RealConnection candidate = findConnection(
          connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled);
      if (connection.isHealthy(doExtensiveHealthChecks)) {
        return candidate;
      }
      synchronized (connectionPool) {
        connection.noNewStreams = true;
        connection.allocationCount--;
        connection = null;
      }
    }
  }

  /**
   * Returns a connection to host a new stream. This prefers the existing connection if it exists,
   * then the pool, finally building a new connection.
   */
  private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout,
      boolean connectionRetryEnabled) throws IOException, RouteException {
    synchronized (connectionPool) {
      if (released) throw new IllegalStateException("released");
      if (stream != null) throw new IllegalStateException("stream != null");
      if (canceled) throw new IOException("Canceled");

      RealConnection allocatedConnection = this.connection;
      if (allocatedConnection != null && !allocatedConnection.noNewStreams) {
        return allocatedConnection;
      }
    }

    // Attempt to get a connection from the pool.
    RealConnection pooledConnection = (RealConnection) connectionPool.get(address);
    if (pooledConnection != null) {
      synchronized (connectionPool) {
        this.connection = pooledConnection;
        if (canceled) throw new IOException("Canceled");
        return pooledConnection;
      }
    }

    // Attempt to create a connection.
    synchronized (connectionPool) {
      if (routeSelector == null) {
        routeSelector = new RouteSelector(address, routeDatabase());
      }
    }
    Route route = routeSelector.next();
    RealConnection newConnection = new RealConnection(route);
    synchronized (connectionPool) {
      connectionPool.put(newConnection);
      this.connection = newConnection;
      if (canceled) throw new IOException("Canceled");
    }

    newConnection.connect(connectTimeout, readTimeout, writeTimeout, address.getConnectionSpecs(),
        connectionRetryEnabled);
    routeDatabase().connected(newConnection.getRoute());

    synchronized (connectionPool) {
      // TODO(jwilson): what should this limit be?
      newConnection.allocationLimit = newConnection.isMultiplexed() ? 100 : 1;
      newConnection.allocationCount++;
    }

    return newConnection;
  }

  public void streamFinished(HttpStream stream) {
    synchronized (connectionPool) {
      if (stream == null || stream != this.stream) {
        throw new IllegalStateException("expected " + this.stream + " but was " + stream);
      }
      this.stream = null;
      if (released) {
        connection.allocationCount--;
        connection = null;
      }
    }
  }

  public HttpStream stream() {
    synchronized (connectionPool) {
      return stream;
    }
  }

  private RouteDatabase routeDatabase() {
    return Internal.instance.routeDatabase(connectionPool);
  }

  public RealConnection connection() {
    return connection;
  }

  public void release() {
    synchronized (connectionPool) {
      if (this.released) {
        throw new IllegalStateException("already released");
      }
      released = true;
      if (this.stream == null) {
        connection.allocationCount--;
        connection = null;
      }
    }
  }

  /** Forbid new streams from being created on the connection that hosts this allocation. */
  public void noNewStreamsOnConnection() {
    synchronized (connectionPool) {
      connection.noNewStreams = true;
    }
  }

  /** Forbid new streams from being created on this allocation. */
  public void noNewStreams() {
    synchronized (connectionPool) {
      if (connection != null) {
        connection.noNewStreams = true;
      }
    }
  }

  public void cancel() {
    HttpStream streamToCancel;
    RealConnection connectionToCancel;
    synchronized (connectionPool) {
      canceled = true;
      streamToCancel = stream;
      connectionToCancel = connection;
    }
    if (streamToCancel != null) {
      streamToCancel.cancel();
    } else if (connectionToCancel != null) {
      connectionToCancel.cancel();
    }
  }

  private void connectionFailed(IOException e) {
    synchronized (connectionPool) {
      if (connection.streamCount == 0 && routeSelector != null) {
        Route failedRoute = connection.getRoute();
        routeSelector.connectFailed(failedRoute, e);
      }
      connection.noNewStreams = true;
      connection.allocationCount--;
      connection = null;
      stream = null;

      // If we saw a failure on a recycled connection, start with a fresh route.
      // TODO(jwilson): confirm that this helps tests to pass!
      routeSelector = null;
    }
  }

  public boolean recover(RouteException e) {
    if (connection != null) {
      connectionFailed(e.getLastConnectException());
    }

    if ((routeSelector != null && !routeSelector.hasNext()) // No more routes to attempt.
        || !isRecoverable(e)) {
      return false;
    }

    return true;
  }

  public boolean recover(IOException e, Sink requestBodyOut) {
    if (connection != null) {
      connectionFailed(e);
    }

    boolean canRetryRequestBody = requestBodyOut == null || requestBodyOut instanceof RetryableSink;
    if ((routeSelector != null && !routeSelector.hasNext()) // No more routes to attempt.
        || !isRecoverable(e)
        || !canRetryRequestBody) {
      return false;
    }

    return true;
  }

  private boolean isRecoverable(IOException e) {
    // If there was a protocol problem, don't recover.
    if (e instanceof ProtocolException) {
      return false;
    }

    // If there was an interruption or timeout, don't recover.
    if (e instanceof InterruptedIOException) {
      return false;
    }

    return true;
  }

  private boolean isRecoverable(RouteException e) {
    // Problems with a route may mean the connection can be retried with a new route, or may
    // indicate a client-side or server-side issue that should not be retried. To tell, we must look
    // at the cause.

    IOException ioe = e.getLastConnectException();

    // If there was a protocol problem, don't recover.
    if (ioe instanceof ProtocolException) {
      return false;
    }

    // If there was an interruption don't recover, but if there was a timeout
    // we should try the next route (if there is one).
    if (ioe instanceof InterruptedIOException) {
      return ioe instanceof SocketTimeoutException;
    }

    // Look for known client-side or negotiation errors that are unlikely to be fixed by trying
    // again with a different route.
    if (ioe instanceof SSLHandshakeException) {
      // If the problem was a CertificateException from the X509TrustManager,
      // do not retry.
      if (ioe.getCause() instanceof CertificateException) {
        return false;
      }
    }
    if (ioe instanceof SSLPeerUnverifiedException) {
      // e.g. a certificate pinning error.
      return false;
    }

    // An example of one we might want to retry with a different route is a problem connecting to a
    // proxy and would manifest as a standard IOException. Unless it is one we know we should not
    // retry, we return true and try a new route.
    return true;
  }

  @Override public String toString() {
    return address.toString();
  }
}
