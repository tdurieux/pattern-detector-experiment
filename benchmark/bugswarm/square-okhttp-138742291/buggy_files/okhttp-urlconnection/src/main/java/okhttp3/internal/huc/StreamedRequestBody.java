/*
 * Copyright (C) 2016 Square, Inc.
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
package okhttp3.internal.huc;

import java.io.IOException;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.internal.http.OneShotRequestBody;
import okhttp3.internal.io.Pipe;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Timeout;

final class StreamedRequestBody extends OutputStreamRequestBody implements OneShotRequestBody {
  final Pipe pipe = new Pipe(8192);
  final BufferedSink sink = Okio.buffer(pipe.sink);
  final long expectedContentLength;
  final OutputStream outputStream;

  StreamedRequestBody(long expectedContentLength) {
    this.expectedContentLength = expectedContentLength;
    this.outputStream = newOutputStream(expectedContentLength, sink);
  }

  @Override OutputStream outputStream() {
    return outputStream;
  }

  @Override Timeout timeout() {
    return sink.timeout();
  }

  @Override public MediaType contentType() {
    return null; // We let the caller provide this in a regular header.
  }

  @Override public long contentLength() throws IOException {
    return expectedContentLength;
  }

  @Override public void writeTo(BufferedSink sink) throws IOException {
    Buffer buffer = new Buffer();
    while (pipe.source.read(buffer, 8192) != -1L) {
      sink.write(buffer, buffer.size());
    }
  }
}
