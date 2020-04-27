package os.demo.web;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import os.demo.web.config.AppConfig;
import os.demo.web.verticles.MainVerticle;


@ExtendWith(VertxExtension.class)
public class VertxTest {
  private Vertx vertx;
  @BeforeEach
  void before(Vertx vertx, VertxTestContext context) {
    this.vertx=vertx;
    vertx.deployVerticle(new MainVerticle(), context.completing());
  }

  @AfterEach
  public void finish(VertxTestContext testContext) {
    vertx.close(testContext.succeeding(response -> {
      testContext.completeNow();
    }));
  }


  @Test
  void testStatusEndpoint(Vertx vertx, VertxTestContext context) {

    WebClient client = WebClient.create(vertx);

    client.get(8080, "localhost", "/ping")
      .as(BodyCodec.string())
      .send(context.succeeding(resp -> context.verify(() -> {
        Assertions.assertEquals("OK", resp.body());
        context.completeNow();
      })));

  }

}
