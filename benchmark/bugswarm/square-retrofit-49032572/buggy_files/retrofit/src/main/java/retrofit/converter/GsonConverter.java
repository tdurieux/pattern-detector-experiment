/*
 * Copyright (C) 2012 Square, Inc.
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
package retrofit.converter;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * A {@link Converter} which uses GSON for serialization and deserialization of entities.
 */
public class GsonConverter implements Converter {
  private final Gson gson;
  private final Charset charset;

  /**
   * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
   * decoding from JSON (when no charset is specified by a header) will use UTF-8.
   */
  public GsonConverter() {
    this(new Gson());
  }

  /**
   * Create an instance using the supplied {@link Gson} object for conversion. Encoding to JSON and
   * decoding from JSON (when no charset is specified by a header) will use UTF-8.
   */
  public GsonConverter(Gson gson) {
    this(gson, Charset.forName("UTF-8"));
  }

  /**
   * Create an instance using the supplied {@link Gson} object for conversion. Encoding to JSON and
   * decoding from JSON (when no charset is specified by a header) will use the specified charset.
   */
  public GsonConverter(Gson gson, Charset charset) {
    this.gson = gson;
    this.charset = charset;
  }

  @Override public Object fromBody(TypedInput body, Type type) throws IOException {
    Charset charset = this.charset;
    if (body.mediaType() != null) {
      charset = body.mediaType().charset(charset);
    }
    InputStreamReader isr = null;
    try {
      isr = new InputStreamReader(body.in(), charset);
      return gson.fromJson(isr, type);
    } finally {
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  @Override public TypedOutput toBody(Object object, Type type) {
    String json = gson.toJson(object, type);
    return new JsonTypedOutput(json.getBytes(charset), charset);
  }

  private static class JsonTypedOutput implements TypedOutput {
    private final byte[] jsonBytes;
    private final MediaType mediaType;

    JsonTypedOutput(byte[] jsonBytes, Charset charset) {
      this.jsonBytes = jsonBytes;
      this.mediaType = MediaType.parse("application/json; charset=" + charset.name());
    }

    @Override public String fileName() {
      return null;
    }

    @Override public MediaType mediaType() {
      return mediaType;
    }

    @Override public long length() {
      return jsonBytes.length;
    }

    @Override public void writeTo(OutputStream out) throws IOException {
      out.write(jsonBytes);
    }
  }
}
