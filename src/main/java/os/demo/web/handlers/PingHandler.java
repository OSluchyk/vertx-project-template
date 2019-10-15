package os.demo.web.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class PingHandler implements Handler<RoutingContext> {

  public static final String message = "pong";

  public void handle(RoutingContext context) {
    context.response().setStatusCode(200).end(message);
  }

}
