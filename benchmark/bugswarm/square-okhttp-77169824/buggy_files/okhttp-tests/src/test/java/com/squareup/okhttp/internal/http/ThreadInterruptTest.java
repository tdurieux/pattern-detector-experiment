/*
 * Copyright (C) 2014 Square, Inc.
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

import com.squareup.okhttp.DelegatingServerSocketFactory;
import com.squareup.okhttp.DelegatingSocket;
import com.squareup.okhttp.DelegatingSocketFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.okhttp.internal.Internal;
import com.squareup.okhttp.internal.Network;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okio.Buffer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class ThreadInterruptTest {

  // The size of the socket buffers in bytes.
  private static final int SOCKET_BUFFER_SIZE = 256 * 1024;

  // 203.0.113.255 reserved for documentation.
  private static final byte[] IPV4_BLACKHOLE_ADDRESS =
      new byte[] { (byte) 203, 0, 113, (byte) 255 };

  // 2001:db8::1 reserved for documentation.
  public static final byte[] IPV6_BLACKHOLE_ADDRESS =
      new byte[] { 0x20, 0x01, 0x0d, (byte) 0xb8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };

  private MockWebServer server;
  private OkHttpClient client;

  @Before public void setUp() throws Exception {
    server = new MockWebServer();
    client = new OkHttpClient();

    // Sockets on some platforms can have large buffers that mean writes do not block when
    // required. These socket factories explicitly set the buffer sizes on sockets created.
    server.setServerSocketFactory(
        new DelegatingServerSocketFactory(ServerSocketFactory.getDefault()) {
          @Override
          protected ServerSocket configureServerSocket(ServerSocket serverSocket)
              throws IOException {
            serverSocket.setReceiveBufferSize(SOCKET_BUFFER_SIZE);
            return serverSocket;
          }
        });
    client.setSocketFactory(new DelegatingSocketFactory(SocketFactory.getDefault()) {
      @Override
      protected Socket configureSocket(Socket socket) throws IOException {
        socket.setSendBufferSize(SOCKET_BUFFER_SIZE);
        socket.setReceiveBufferSize(SOCKET_BUFFER_SIZE);
        return socket;
      }
    });
  }

  @Test @Ignore public void interruptConnect() throws Exception {
    // Neither the OpenJDK nor Android implement interruption of Socket.connect() using
    // Thread.interrupt().
    fail();
  }

  // Connect timeouts throw InterruptedIOException (actually SocketTimeoutException).
  // Interruptions should terminate connection attempts, but timeouts should not.
  @Test public void timeoutConnect() throws Exception {
    InetAddress localAddress = getNonLoopbackLocalAddress();
    InetAddress blackholeAddress = getBlackholeAddress(localAddress);
    // Attempt to connect twice: once using an address that will timeout, once on the real
    // address.
    Internal.instance.setNetwork(client,
        new FakeNetwork(new InetAddress[] { blackholeAddress, localAddress }));
    TimeoutCountingSocketFactory clientSocketFactory = new TimeoutCountingSocketFactory();
    client.setSocketFactory(clientSocketFactory);

    server.enqueue(new MockResponse());
    server.start(localAddress, 0);

    HttpURLConnection connection = new OkUrlFactory(client).open(server.getUrl("/"));
    connection.setConnectTimeout(10);
    assertEquals(200, connection.getResponseCode());
    clientSocketFactory.assertTimeoutCount(1);

    connection.disconnect();
  }

  @Test public void interruptWritingRequestBody() throws Exception {
    int requestBodySize = 2 * 1024 * 1024; // 2 MiB

    server.enqueue(new MockResponse()
        .throttleBody(64 * 1024, 125, TimeUnit.MILLISECONDS)); // 500 Kbps
    server.start();

    interruptLater(500);

    HttpURLConnection connection = new OkUrlFactory(client).open(server.getUrl("/"));
    connection.setDoOutput(true);
    connection.setFixedLengthStreamingMode(requestBodySize);
    OutputStream requestBody = connection.getOutputStream();
    byte[] buffer = new byte[1024];
    try {
      for (int i = 0; i < requestBodySize; i += buffer.length) {
        requestBody.write(buffer);
        requestBody.flush();
      }
      fail("Expected thread to be interrupted");
    } catch (InterruptedIOException expected) {
    }

    connection.disconnect();
  }

  @Test public void interruptReadingResponseBody() throws Exception {
    int responseBodySize = 2 * 1024 * 1024; // 2 MiB

    server.enqueue(new MockResponse()
        .setBody(new Buffer().write(new byte[responseBodySize]))
        .throttleBody(64 * 1024, 125, TimeUnit.MILLISECONDS)); // 500 Kbps
    server.start();

    interruptLater(500);

    HttpURLConnection connection = new OkUrlFactory(client).open(server.getUrl("/"));
    InputStream responseBody = connection.getInputStream();
    byte[] buffer = new byte[1024];
    try {
      while (responseBody.read(buffer) != -1) {
      }
      fail("Expected thread to be interrupted");
    } catch (InterruptedIOException expected) {
    }

    connection.disconnect();
  }

  private void interruptLater(final int delayMillis) {
    final Thread toInterrupt = Thread.currentThread();
    Thread interruptingCow = new Thread() {
      @Override public void run() {
        try {
          sleep(delayMillis);
          toInterrupt.interrupt();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    };
    interruptingCow.start();
  }

  private static class FakeNetwork implements Network {
    private final InetAddress[] addresses;

    public FakeNetwork(InetAddress[] addresses) {
      this.addresses = addresses;
    }

    @Override public InetAddress[] resolveInetAddresses(String host) throws UnknownHostException {
      return addresses;
    }
  }

  private static InetAddress getNonLoopbackLocalAddress() throws SocketException {
    InetAddress localAddress = null;
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    outer: while (interfaces.hasMoreElements()) {
      NetworkInterface networkInterface = interfaces.nextElement();
      if (networkInterface.isUp() && !networkInterface.isLoopback()) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          localAddress = addresses.nextElement();
          break outer;
        }
      }
    }
    if (localAddress == null) {
      fail("No network");
    }
    return localAddress;
  }

  private static InetAddress getBlackholeAddress(InetAddress localAddress)
      throws UnknownHostException {
    byte[] addressBytes =
        localAddress instanceof Inet4Address ? IPV4_BLACKHOLE_ADDRESS : IPV6_BLACKHOLE_ADDRESS;
    return InetAddress.getByAddress(addressBytes);
  }

  private static class TimeoutCountingSocketFactory extends DelegatingSocketFactory {
    private final AtomicInteger timeoutCount = new AtomicInteger(0);

    public void assertTimeoutCount(int expected) {
      assertEquals(expected, timeoutCount.get());
    }

    public TimeoutCountingSocketFactory() {
      super(SocketFactory.getDefault());
    }

    @Override protected Socket configureSocket(Socket socket) throws IOException {
      return new TimeoutCountingSocket(socket);
    }

    private class TimeoutCountingSocket extends DelegatingSocket {
      public TimeoutCountingSocket(Socket delegate) {
        super(delegate);
      }

      @Override public void connect(SocketAddress remoteAddr) throws IOException {
        try {
          super.connect(remoteAddr);
        } catch (SocketTimeoutException e) {
          timeoutCount.incrementAndGet();
          throw e;
        }
      }

      @Override public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
        try {
          super.connect(remoteAddr, timeout);
        } catch (SocketTimeoutException e) {
          timeoutCount.incrementAndGet();
          throw e;
        }
      }
    }
  }
}
