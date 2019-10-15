package os.demo.web.verticles;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import os.demo.web.handlers.AppStatusHandler;
import os.demo.web.handlers.CorsHandler;
import os.demo.web.handlers.DelayedServiceHandler;
import os.demo.web.handlers.PingHandler;

import static os.demo.web.verticles.Routing.Endpoints.*;


public class Routing {

  public Router initRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.configure());

    setResourceNotFoundLogger(router);
    BodyHandler bodyHandler = BodyHandler.create().setMergeFormAttributes(true);
    AppStatusHandler appStatusHandler =new AppStatusHandler();
    router.put(ALL).handler(bodyHandler);
    router.post(ALL).handler(bodyHandler);
    router.get(STATUS).handler(appStatusHandler);
    router.get(PING).handler(new PingHandler());
    router.get(SLEEP).handler(new DelayedServiceHandler());

    return router;
  }

  private void setResourceNotFoundLogger(Router router) {
    router.route().last().handler(frc -> {
      frc.next();
    });
  }

  public static class Endpoints {
    public static String ALL = "/*";
    public static String STATUS = "/status";
    public static String PING = "/ping";
    public static String SLEEP = "/sleep/:ms";

    private Endpoints() {
    }
  }


}
