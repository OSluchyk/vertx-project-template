package os.demo.web;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    configRetriever(vertx).getConfig(ar -> {
      if (ar.failed()) {
        startPromise.fail("Failed to retrieve the configuration");
      } else {
        JsonObject config = ar.result();

        HttpServerOptions options = new HttpServerOptions()
          .setTcpKeepAlive(config.getBoolean("tcpKeepAlive"))
          .setIdleTimeout(config.getInteger("idleTimeout"))
          .setReuseAddress(config.getBoolean("reuseAddress"))
          .setReusePort(config.getBoolean("reusePort"));

        Integer port = config.getInteger("port");
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

  private ConfigRetriever configRetriever(Vertx vertx) {
    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", "config.json"))
      .setOptional(true);

    ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");


    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
      .setIncludeDefaultStores(true)
      .addStore(fileStore)
      .addStore(sysPropsStore);

    return ConfigRetriever.create(vertx, options);
  }
}
