package os.demo.web;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(VertxExtension.class)
public class VertxTest {
  @BeforeEach
  void before(Vertx vertx, VertxTestContext context) {
    vertx.deployVerticle(new MainVerticle(), context.completing());
  }

  @Test
  void testStatusEndpoint(Vertx vertx, VertxTestContext context) {

    WebClient client = WebClient.create(vertx);

    client.get(8080, "localhost", "/status")
      .as(BodyCodec.string())
      .send(context.succeeding(resp -> context.verify(() -> {
        Assertions.assertEquals("OK", resp.body());
        context.completeNow();
      })));

    //assertions...
  }

}
