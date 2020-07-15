package com.ilegra.laa.verticles;

import com.ilegra.laa.builders.LogRequestBuilder;
import com.ilegra.laa.models.AwsRegion;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.ResponseModel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServerVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

  public static Optional<String> portCloud = Optional.ofNullable(System.getenv("PORT"));
  private static String VALID_URL_REGEX = "(\\/.+)+\\s(\\d+)\\s([\\w\\-?]+)\\s([1-3])";
  private static String REPLACE_IDS_IN_URL_REGEX = "(?<=\\/)\\d+(?=\\/?)";
  private static String ID_REPLACEMENT = "{id}";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.post("/laa/ingest").handler(this::handleLogIngestion);

    vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(portCloud.orElse("8080")), res -> {
      if (res.succeeded()) {
        LOG.info("Server is now listening! ".concat("http://localhost:")
          .concat(String.valueOf(res.result().actualPort())
            .concat("/laa")));
      } else {
        LOG.error("Failed to bind!");
      }
    });

  }

  private void handleLogIngestion(RoutingContext routingContext){
    String ingestedLog = routingContext.getBodyAsString();
    Optional<LogRequest> logRequest = this.parseLog(ingestedLog);
    logRequest.ifPresentOrElse( log -> {
      LOG.debug("Log parsed successfully: {}", logRequest.get());
      routingContext.response()
        .end(Json.encodePrettily(
          new ResponseModel(routingContext.statusCode(), "Log processed successfully"))
        );
    }, () -> {
      routingContext.response()
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end(Json.encodePrettily(
          new ResponseModel(HttpResponseStatus.BAD_REQUEST.code(), "Invalid log format"))
        );
    });
  }


  private Optional<LogRequest> parseLog(String ingestedLog) {
    Pattern pattern = Pattern.compile(VALID_URL_REGEX);
    Matcher matcher = pattern.matcher(ingestedLog);
    if(matcher.find()) {
      Optional<AwsRegion> region = AwsRegion.from(Integer.valueOf(matcher.group(4)));
      if (region.isPresent()) {
        LocalDateTime date = LocalDateTime.ofInstant(
          Instant.ofEpochSecond(Long.parseLong(matcher.group(2))),
          ZoneOffset.UTC);
        return Optional.of(
          LogRequest
            .builder()
            .setUrl(matcher.group(1).replaceAll(REPLACE_IDS_IN_URL_REGEX, ID_REPLACEMENT))
            .setDate(date)
            .setClientId(matcher.group(3))
            .setRegion(region.get())
            .createLogRequest()
        );
      }
    }
    return Optional.empty();
  }
}
