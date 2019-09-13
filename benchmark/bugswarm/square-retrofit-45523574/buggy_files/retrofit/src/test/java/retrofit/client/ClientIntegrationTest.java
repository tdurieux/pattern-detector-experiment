// Copyright 2014 Square, Inc.
package retrofit.client;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

import static org.assertj.core.api.Assertions.assertThat;
import static retrofit.TestingUtils.assertTypedBytes;

@RunWith(Parameterized.class)
public final class ClientIntegrationTest {
  @Parameterized.Parameters
  public static List<Object[]> clients() {
    return Arrays.asList(new Object[][] {
        { new OkClient() },
        { new UrlConnectionClient() },
        { new ApacheClient() }
    });
  }

  @Parameterized.Parameter
  public Client client;

  @Rule public final MockWebServerRule server = new MockWebServerRule();

  private Service service;

  @Before public void setUp() throws Exception {
    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(server.getUrl("/").toString())
        .setClient(client)
        .build();
    service = restAdapter.create(Service.class);
  }

  public interface Service {
    @GET("/get")
    Response get();

    @POST("/post")
    Response post(@Body List<String> body);
  }

  @Test public void get() throws InterruptedException {
    server.enqueue(new MockResponse().setBody("{}"));

    Response response = service.get();
    assertTypedBytes(response.getBody(), "{}");

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/get");
    assertThat(request.getBody()).isEmpty();
  }

  @Test public void post() throws InterruptedException {
    server.enqueue(new MockResponse().setBody("{}"));

    Response response = service.post(Arrays.asList("Hello", "World!"));
    assertTypedBytes(response.getBody(), "{}");

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/post");
    assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    assertThat(request.getHeader("Content-Length")).isEqualTo("18");
    assertThat(request.getUtf8Body()).isEqualTo("[\"Hello\",\"World!\"]");
  }
}
