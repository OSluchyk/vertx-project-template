package os.demo.web.handlers;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.Properties;

public class AppStatusHandler implements Handler<RoutingContext> {
  private Buffer response;

  public AppStatusHandler() {
    Properties properties = new Properties();
    try {
      properties.load(getClass().getResourceAsStream("/version.txt"));

    } catch (IOException e) {
      e.printStackTrace();
    }

    this.response = Buffer.buffer(new JsonObject()
      .put("version", properties.getProperty("version", "unknown"))
      .put("buildDate", properties.getProperty("build.date", "unknown"))
      .encode());
  }

  @Override
  public void handle(RoutingContext event) {

    event.response().putHeader("content-type", "application/json").end(response);
  }
}
