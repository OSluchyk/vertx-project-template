package os.demo.web.handlers;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static io.vertx.core.http.HttpHeaders.*;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;

public class CorsHandler implements io.vertx.ext.web.handler.CorsHandler {

  private final Pattern allowedOrigin;

  private String allowedMethodsString;
  private String allowedHeadersString;
  private String exposedHeadersString;
  private boolean allowCredentials;
  private String maxAgeSeconds;
  private final Set<HttpMethod> allowedMethods = new LinkedHashSet<>();
  private final Set<String> allowedHeaders = new LinkedHashSet<>();
  private final Set<String> exposedHeaders = new LinkedHashSet<>();

  public static io.vertx.ext.web.handler.CorsHandler configure() {
    return new CorsHandler("*")
      .allowCredentials(true)
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.HEAD)
      .allowedMethod(HttpMethod.POST)
      .allowedHeader(HttpHeaderNames.CONTENT_TYPE.toString());
  }

  private CorsHandler(String allowedOriginPattern) {
    Objects.requireNonNull(allowedOriginPattern);
    if ("*".equals(allowedOriginPattern)) {
      allowedOrigin = null;
    } else {
      allowedOrigin = Pattern.compile(allowedOriginPattern);
    }
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler allowedMethod(HttpMethod method) {
    allowedMethods.add(method);
    allowedMethodsString = join(allowedMethods);
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler allowedMethods(Set<HttpMethod> methods) {
    allowedMethods.addAll(methods);
    allowedMethodsString = join(allowedMethods);
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler allowedHeader(String headerName) {
    allowedHeaders.add(headerName);
    allowedHeadersString = join(allowedHeaders);
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler allowedHeaders(Set<String> headerNames) {
    allowedHeaders.addAll(headerNames);
    allowedHeadersString = join(allowedHeaders);
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler exposedHeader(String headerName) {
    exposedHeaders.add(headerName);
    exposedHeadersString = join(exposedHeaders);
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler exposedHeaders(Set<String> headerNames) {
    exposedHeaders.addAll(headerNames);
    exposedHeadersString = join(exposedHeaders);
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler allowCredentials(boolean allow) {
    this.allowCredentials = allow;
    return this;
  }

  @Override
  public io.vertx.ext.web.handler.CorsHandler maxAgeSeconds(int maxAgeSeconds) {
    this.maxAgeSeconds = maxAgeSeconds == -1 ? null : String.valueOf(maxAgeSeconds);
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    HttpServerRequest request = context.request();
    HttpServerResponse response = context.response();
    String origin = context.request().headers().get(ORIGIN);
    if (origin == null) {
      // Not a CORS request - we don't set any headers and just call the next handler
      context.next();
    } else if (isValidOrigin(origin)) {
      String accessControlRequestMethod = request.headers().get(ACCESS_CONTROL_REQUEST_METHOD);
      if (request.method() == HttpMethod.OPTIONS && accessControlRequestMethod != null) {
        // Pre-flight request
        addCredentialsAndOriginHeader(response, origin);
        if (allowedMethodsString != null) {
          response.putHeader(ACCESS_CONTROL_ALLOW_METHODS, allowedMethodsString);
        }
        if (allowedHeadersString != null) {
          response.putHeader(ACCESS_CONTROL_ALLOW_HEADERS, allowedHeadersString);
        }
        if (maxAgeSeconds != null) {
          response.putHeader(ACCESS_CONTROL_MAX_AGE, maxAgeSeconds);
        }
        response.setStatusCode(204).end();
      } else {
        addCredentialsAndOriginHeader(response, origin);
        if (exposedHeadersString != null) {
          response.putHeader(ACCESS_CONTROL_EXPOSE_HEADERS, exposedHeadersString);
        }
        context.next();
      }
    } else {
      sendInvalid(request.response());
    }
  }

  private void addCredentialsAndOriginHeader(HttpServerResponse response, String origin) {
    if (allowCredentials) {
      response.putHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      // Must be exact origin (not '*') in case of credentials
      response.putHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    } else {
      // Can be '*' too
      response.putHeader(ACCESS_CONTROL_ALLOW_ORIGIN, getAllowedOrigin(origin));
    }
  }

  private void sendInvalid(HttpServerResponse resp) {
    resp.setStatusCode(403).setStatusMessage("CORS Rejected - Invalid origin").end();
  }

  private boolean isValidOrigin(String origin) {
    if (allowedOrigin == null) {
      // Null means accept all origins
      return true;
    }
    return allowedOrigin.matcher(origin).matches();
  }

  private String getAllowedOrigin(String origin) {
    return allowedOrigin == null ? "*" : origin;
  }

  private String join(Collection<?> ss) {
    if (ss == null || ss.isEmpty()) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object s : ss) {
      if (!first) {
        sb.append(',');
      }
      sb.append(s);
      first = false;
    }
    return sb.toString();
  }

}
