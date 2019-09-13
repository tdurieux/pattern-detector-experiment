// Copyright 2013 Square, Inc.
package retrofit;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import okio.Buffer;
import okio.Okio;
import retrofit.mime.MimeHelper;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import static org.assertj.core.api.Assertions.assertThat;

public final class TestingUtils {
  public static Method onlyMethod(Class c) {
    Method[] declaredMethods = c.getDeclaredMethods();
    if (declaredMethods.length == 1) {
      return declaredMethods[0];
    }
    throw new IllegalArgumentException("More than one method declared.");
  }

  public static TypedOutput createMultipart(Map<String, TypedOutput> parts) {
    MultipartTypedOutput typedOutput = MimeHelper.newMultipart("foobarbaz");
    for (Map.Entry<String, TypedOutput> part : parts.entrySet()) {
      typedOutput.addPart(part.getKey(), part.getValue());
    }
    return typedOutput;
  }

  public static void assertBytes(byte[] bytes, String expected) throws IOException {
    assertThat(new String(bytes, "UTF-8")).isEqualTo(expected);
  }

  public static void assertTypedBytes(TypedOutput bytes, String expected) {
    assertThat(bytes).isNotNull();
    try {
      Buffer buffer = new Buffer();
      bytes.writeTo(buffer.outputStream());
      assertThat(buffer.readUtf8()).isEqualTo(expected);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void assertTypedBytes(TypedInput bytes, String expected) {
    assertThat(bytes).isNotNull();
    try {
      String actual = Okio.buffer(Okio.source(bytes.in())).readUtf8();
      assertThat(actual).isEqualTo(expected);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
