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

        // check for blocked threads every 5s
        options.setBlockedThreadCheckInterval(5);
        options.setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS);

        // warn if an worker thread handler took more than 10s to execute
        options.setMaxWorkerExecuteTime(10);
        options.setMaxWorkerExecuteTimeUnit(TimeUnit.SECONDS);

        // log the stack trace if an event loop or worker handler took more than 20s to execute
        options.setWarningExceptionTime(20);
        options.setWarningExceptionTimeUnit(TimeUnit.SECONDS);



        logger.info("Vertx options: {}", options);
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        int instances = deploymentOptions.getInstances();
        if (instances < 0) {
            instances = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
        }

        if (deploymentOptions.isWorker()) {
            //Worker verticles handle events on worker threads
            // but a single instance can only handle one event at a given point in time.
            // So you shall deploy as many instances as worker threads if you want to leverage all of them
            deploymentOptions.setWorkerPoolSize(instances);
        }
        deploymentOptions.setInstances(instances);
        logger.info(() -> "Deployment Options: " + deploymentOptions.toJson().encode());
        logger.info("{} VertX instances will be launched", instances);

    }
}
