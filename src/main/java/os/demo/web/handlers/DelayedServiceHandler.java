package os.demo.web.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class DelayedServiceHandler implements Handler<RoutingContext> {
  private final long timeoutMs = 1;

  @Override
  public void handle(RoutingContext routingContext) {
    String ms = routingContext.pathParam("ms");
    routingContext.vertx().setTimer(Long.parseLong(ms), t -> {
      routingContext.response().end("Slept " + ms + " ms");
    });
  }
}
