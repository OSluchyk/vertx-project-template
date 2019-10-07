package os.demo.web;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import os.demo.web.handler.CorsHandler;
import os.demo.web.handler.DelayedServiceHandler;

import static os.demo.web.handler.Routes.*;

public class Routing {

  public Router initRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.configure());

    setResourceNotFoundLogger(router);
    BodyHandler bodyHandler = BodyHandler.create().setMergeFormAttributes(true);
    router.put(ALL).handler(bodyHandler);
    router.post(ALL).handler(bodyHandler);
    router.get(STATUS).handler(routingContext -> routingContext.response().end("OK"));
    router.get(SLEEP).handler(new DelayedServiceHandler());

    return router;
  }

  private void setResourceNotFoundLogger(Router router) {
    router.route().last().handler(frc -> {
      frc.next();
    });
  }


}
