package os.demo.web.verticles;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import os.demo.web.AppLauncher;
import os.demo.web.handlers.StatusHandler;

import static os.demo.web.verticles.Endpoints.ALL;


public class MainVerticle extends BaseVerticle {
    public static void main(String[] args) {
        AppLauncher.executeCommand("run", MainVerticle.class.getName());
    }

    @Override
    protected Router configureRouter(Vertx vertx, Router router) {
        router.get(ALL).handler(new StatusHandler());
        return router;
    }
}
