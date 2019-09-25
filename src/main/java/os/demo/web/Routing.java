package os.demo.web;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import os.demo.web.handler.CorsHandler;

public class Routing {

  public Router initRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create());

    setResourceNotFoundLogger(router);
    BodyHandler bodyHandler = BodyHandler.create().setMergeFormAttributes(true);
    router.put("/*").handler(bodyHandler);
    router.post("/*").handler(bodyHandler);
    router.get("/status").handler(routingContext -> routingContext.response().end("OK"));

    return router;
  }

  private void setResourceNotFoundLogger(Router router) {
    router.route().last().handler(frc -> {
      frc.next();
    });
  }


}
