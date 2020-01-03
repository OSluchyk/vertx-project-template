package os.demo.web.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import os.demo.web.config.AppConfig;
import os.demo.web.handlers.StatusHandler;

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LogManager.getLogger(MainVerticle.class);


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
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
                Routing routing = new Routing();
                vertx.createHttpServer(options)
                        .requestHandler(routing.initRouter(vertx))
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
}
