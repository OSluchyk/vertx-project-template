package os.demo.web.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import os.demo.web.config.AppConfig;
import os.demo.web.handlers.CorsHandlerImpl;

import static os.demo.web.verticles.Endpoints.ALL;


public abstract class BaseVerticle extends AbstractVerticle {
    protected final Logger logger = LogManager.getLogger(getClass());

    @Override
    public void start(Promise<Void> startPromise) {
        AppConfig.get(vertx).setHandler(result -> {
            if (result.failed()) {
                startPromise.fail("Failed to retrieve the configuration");
            } else {
                AppConfig config = result.result();

                HttpServerOptions options = new HttpServerOptions()
                        .setTcpKeepAlive(config.getTcpKeepAlive())
                        .setIdleTimeout(config.getIdleTimeout())
                        .setReuseAddress(config.getReuseAddress())
                        .setReusePort(config.getReusePort())
                        .setTcpFastOpen(true)
                        .setTcpNoDelay(true)
                        .setTcpQuickAck(true);

                Integer port = config.getPort();

                Router router = Router.router(vertx);
                router.route().handler(CorsHandlerImpl.configure());
                BodyHandler bodyHandler = BodyHandler.create().setMergeFormAttributes(true);
                router.put(ALL).handler(bodyHandler);


                vertx.createHttpServer(options)
                        .requestHandler(configureRouter(vertx, router))
                        .listen(port, http -> {
                            if (http.succeeded()) {
                                logger.info("HTTP server started on port {}", port);
                                startPromise.complete();
                            } else {
                                logger.error("Failed to start Web Service : {}", http.cause().getMessage(), http.cause());
                                startPromise.fail(http.cause());
                            }
                        });

            }
        });

    }

    protected abstract Router configureRouter(Vertx vertx, Router router);

}
