package os.demo.web.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FailureHandler implements Handler<RoutingContext> {
  private static final Logger logger = LogManager.getLogger(FailureHandler.class);

  public void handle(RoutingContext context) {
    Throwable thrown = context.failure();
    recordError(thrown);
    context.response().setStatusCode(500).end();
  }

  private void recordError(Throwable throwable) {
    logger.error(throwable.getMessage(), throwable);
  }
}
