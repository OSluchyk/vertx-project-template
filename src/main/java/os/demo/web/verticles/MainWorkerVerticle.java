package os.demo.web.verticles;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import os.demo.web.handlers.SlowStatusHandler;

import static io.vertx.core.Launcher.executeCommand;
import static os.demo.web.verticles.Endpoints.ALL;

public class MainWorkerVerticle extends BaseVerticle {
    public static void main(String[] args) {
        executeCommand("run", "--worker", MainVerticle.class.getName());
    }

    @Override
    protected Router configureRouter(Vertx vertx, Router router) {
        router.get(ALL).handler(new SlowStatusHandler());
        return router;
    }
}
