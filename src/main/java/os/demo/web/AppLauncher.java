package os.demo.web;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class AppLauncher extends Launcher {
    private static final Logger logger = LogManager.getLogger(AppLauncher.class);

    public static void main(String[] args) {
        new AppLauncher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        options.setPreferNativeTransport(true);

        // warn if an event loop thread handler took more than 10ms to execute
        options.setMaxEventLoopExecuteTime(10);
        options.setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS);

        logger.info("Vertx options: {}", options);
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        int instances = deploymentOptions.getInstances();
        if (instances < 0) {
            instances = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
            deploymentOptions.setInstances(instances);
        }
        logger.info(() -> "Deployment Options: " + deploymentOptions.toJson().encode());
    }
}
