package os.demo.web.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import os.demo.web.config.VerticleConfig;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    VerticleConfig.get(vertx).setHandler(result -> {
      if (result.failed()) {
        startPromise.fail("Failed to retrieve the configuration");
      } else {
        VerticleConfig config = result.result();

        HttpServerOptions options = new HttpServerOptions()
          .setTcpKeepAlive(config.getTcpKeepAlive())
          .setIdleTimeout(config.getIdleTimeout())
          .setReuseAddress(config.getReuseAddress())
          .setReusePort(config.getReusePort());

        Integer port = config.getPort();
        vertx.createHttpServer(options).requestHandler(new Routing().initRouter(vertx))
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
