package os.demo.web.verticles;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import os.demo.web.config.AppConfig;

import java.util.HashSet;
import java.util.Set;

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
                        .setTcpQuickAck(true)
                        .setAcceptBacklog(10_000);

                Integer port = config.getPort();

                Router router = Router.router(vertx);
                enableCors(router);
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

    private void enableCors(Router router) {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString());
        allowedHeaders.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString());
        allowedHeaders.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString());
        allowedHeaders.add(HttpHeaderNames.ORIGIN.toString());
        allowedHeaders.add(HttpHeaderNames.CONTENT_TYPE.toString());
        allowedHeaders.add(HttpHeaderNames.ACCEPT.toString());

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.PUT);

        CorsHandler corsHandler = CorsHandler.create(".*")
                .allowCredentials(true)
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods);

        router.route().handler(corsHandler);
    }

    protected abstract Router configureRouter(Vertx vertx, Router router);

}
