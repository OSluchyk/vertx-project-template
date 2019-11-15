package os.demo.web.handlers;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.Properties;

public class AppStatusHandler extends AbstractHandler {
  private Buffer response;

  public AppStatusHandler() {
    Properties properties = new Properties();
    try {
      properties.load(getClass().getResourceAsStream("/version.txt"));

    } catch (IOException e) {
      e.printStackTrace();
    }

    this.response = Buffer.factory.directBuffer(new JsonObject()
      .put("version", properties.getProperty("version", "unknown"))
      .put("buildDate", properties.getProperty("build.date", "unknown"))
      .encode(), "UTF-8");
  }

  @Override
  public void handle(RoutingContext event) {
    event.response().putHeader(HEADER_CONTENT_TYPE, RESPONSE_TYPE_JSON).end(response);
  }
}
