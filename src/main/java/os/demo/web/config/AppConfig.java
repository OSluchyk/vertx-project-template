package os.demo.web.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppConfig {
  private static final Logger logger = LogManager.getLogger(AppConfig.class);

  private final JsonObject configuration;

  public AppConfig(JsonObject configuration) {
    this.configuration = configuration;
  }

  public boolean getTcpKeepAlive() {
    return configuration.getBoolean("tcpKeepAlive");
  }

  public int getIdleTimeout() {
    return configuration.getInteger("idleTimeout");
  }

  public boolean getReuseAddress() {
    return configuration.getBoolean("reuseAddress");
  }

  public boolean getReusePort() {
    return configuration.getBoolean("reusePort");
  }

  public Integer getPort() {
    return configuration.getInteger("port");
  }


  public static Future<AppConfig> get(Vertx vertx) {
    return get(System.getProperty("config.file"), vertx);

  }


  public static Future<AppConfig> get(String externalConfFile, Vertx vertx) {
    Promise<AppConfig> future = Promise.promise();

    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
      .setIncludeDefaultStores(true);

    if (null != externalConfFile) {
      ConfigStoreOptions fileStore = new ConfigStoreOptions()
        .setType("file")
        .setConfig(new JsonObject().put("path", externalConfFile))
        .setOptional(true);
      options.addStore(fileStore);
    }

    options.addStore(new ConfigStoreOptions().setType("sys"));

    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(ar -> {
      if (ar.succeeded()) {
        JsonObject configuration = ar.result();
        logger.info("Result configuration: {}", configuration.encodePrettily());
        future.complete(new AppConfig(configuration));
      } else {
        logger.error("Failed to read configuration", ar.cause());
        future.fail(ar.cause());
      }
    });
    return future.future();
  }



}
