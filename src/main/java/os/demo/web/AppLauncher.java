package os.demo.web;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLauncher extends Launcher {
  private static final Logger logger = LogManager.getLogger(AppLauncher.class);

  public static void main(String[] args) {
    new AppLauncher().dispatch(args);
  }

  @Override
  public void beforeStartingVertx(VertxOptions options) {
    options.setPreferNativeTransport(true);

    logger.info(options.toString());
  }

  @Override
  public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
    logger.info("Deployment Options: {}",deploymentOptions.toJson().encode());
  }
}
