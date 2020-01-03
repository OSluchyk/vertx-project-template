package os.demo.web.handlers;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractHandler implements Handler<RoutingContext> {

    static final CharSequence HEADER_CONTENT_TYPE = HttpHeaders.createOptimized("content-type");
    static final CharSequence HEADER_CONTENT_LENGTH = HttpHeaders.createOptimized("content-length");

    static final CharSequence RESPONSE_TYPE_PLAIN = HttpHeaders.createOptimized("text/plain");
    static final CharSequence RESPONSE_TYPE_HTML = HttpHeaders.createOptimized("text/html; charset=UTF-8");
    static final CharSequence RESPONSE_TYPE_JSON = HttpHeaders.createOptimized("application/json");
}
