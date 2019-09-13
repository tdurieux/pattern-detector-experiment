/*
 * Copyright (C) 2012 Square, Inc.
 * Copyright (C) 2007 The Guava Authors
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
package retrofit;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Source;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

final class Utils {
  static <T> T checkNotNull(T object, String message, Object... args) {
    if (object == null) {
      throw new NullPointerException(String.format(message, args));
    }
    return object;
  }

  /**
   * Replace a {@link Response} with an identical copy whose body is backed by a
   * {@link Buffer} rather than a {@link Source}.
   */
  static Response readBodyToBytesIfNecessary(Response response) throws IOException {
    final ResponseBody body = response.body();

    BufferedSource source = body.source();
    final Buffer buffer = new Buffer();
    buffer.writeAll(source);
    source.close();

    return response.newBuilder()
        .body(new ResponseBody() {
          @Override public MediaType contentType() {
            return body.contentType();
          }

          @Override public long contentLength() {
            return buffer.size();
          }

          @Override public BufferedSource source() {
            return buffer;
          }
        })
        .build();
  }

  static <T> void validateServiceClass(Class<T> service) {
    if (!service.isInterface()) {
      throw new IllegalArgumentException("Only interface endpoint definitions are supported.");
    }
    // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
    // Android (http://b.android.com/58753) but it forces composition of API declarations which is
    // the recommended pattern.
    if (service.getInterfaces().length > 0) {
      throw new IllegalArgumentException("Interface definitions must not extend other interfaces.");
    }
  }

  static RequestBody typedOutputToBody(final TypedOutput body) {
    if (body == null) return null;
    return new RequestBody() {
      @Override public MediaType contentType() {
        return body.mediaType();
      }

      @Override public long contentLength() throws IOException {
        return body.length();
      }

      @Override public void writeTo(BufferedSink sink) throws IOException {
        body.writeTo(sink.outputStream());
      }
    };
  }

  static TypedInput typedInputFromBody(final ResponseBody body) {
    if (body == null) return null;
    return new TypedInput() {
      @Override public MediaType mediaType() {
        return body.contentType();
      }

      @Override public long length() {
        return body.contentLength();
      }

      @Override public InputStream in() throws IOException {
        return body.byteStream();
      }
    };
  }

  static class SynchronousExecutor implements Executor {
    @Override public void execute(Runnable runnable) {
      runnable.run();
    }
  }

  private Utils() {
    // No instances.
  }
}
