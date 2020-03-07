package os.demo.web.handlers;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

public class SlowStatusHandler extends AbstractHandler {
    private static final String MESSAGE = "OK";
    private static final Buffer MESSAGE_BUFFER = Buffer.factory.directBuffer(MESSAGE, "UTF-8");
    private static final CharSequence MESSAGE_LENGTH = HttpHeaders.createOptimized("" + MESSAGE.length());
    private static final int DEFAULT_WAIT_TIME_MS = 1000;


    @Override
    public void handle(RoutingContext event) {
        int waitTime = getInt(event.request().getParam("wait"));
        event.vertx()
                .setTimer(
                        waitTime,
                        h -> event.response()
                                .putHeader(HEADER_CONTENT_TYPE, RESPONSE_TYPE_PLAIN)
                                .putHeader(HEADER_CONTENT_LENGTH, MESSAGE_LENGTH)
                                .setStatusCode(200).end(MESSAGE_BUFFER)
                );
    }

    public static int getInt(String param) {
        try {
            return Integer.valueOf(param);
        } catch (NumberFormatException e) {
            return DEFAULT_WAIT_TIME_MS;
        }
    }


}
