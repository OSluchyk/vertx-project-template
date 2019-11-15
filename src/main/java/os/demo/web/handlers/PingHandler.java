package os.demo.web.handlers;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

public class PingHandler extends AbstractHandler {

  private static final String MESSAGE = "Hello, World!";
  private static final Buffer MESSAGE_BUFFER = Buffer.factory.directBuffer(MESSAGE, "UTF-8");
  private static final CharSequence MESSAGE_LENGTH = HttpHeaders.createOptimized("" + MESSAGE.length());


  public void handle(RoutingContext context) {
    context.response()
      .putHeader(HEADER_CONTENT_TYPE, RESPONSE_TYPE_PLAIN)
      .putHeader(HEADER_CONTENT_LENGTH, MESSAGE_LENGTH)
      .setStatusCode(200).end(MESSAGE_BUFFER);
  }

}
