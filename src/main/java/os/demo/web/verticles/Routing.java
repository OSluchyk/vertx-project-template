package os.demo.web.verticles;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import os.demo.web.handlers.CorsHandler;
import os.demo.web.handlers.StatusHandler;

import static os.demo.web.verticles.Routing.Endpoints.ALL;


public class Routing {

    public Router initRouter(Vertx vertx) {
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.configure());

        setResourceNotFoundLogger(router);
        BodyHandler bodyHandler = BodyHandler.create().setMergeFormAttributes(true);
        router.put(ALL).handler(bodyHandler);
        router.get(ALL).handler(new StatusHandler());
        return router;
    }

    private void setResourceNotFoundLogger(Router router) {
        router.route().last().handler(RoutingContext::next);
    }

    public static class Endpoints {
        public static final String ALL = "/*";

        private Endpoints() {
        }
    }


}
