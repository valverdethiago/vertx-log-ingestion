package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServerVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);
  private static final Integer DEFAULT_SEARCH_SIZE = 3;

  public static Optional<String> portCloud = Optional.ofNullable(System.getenv("PORT"));
  private static String VALID_URL_REGEX = "(\\/.+)+\\s(\\d+)\\s([\\w\\-?]+)\\s([1-3])";
  private static String VALID_WEEK_REGEX = "(\\d{1,2})\\-(\\d{1,4})";
  private static String REPLACE_IDS_IN_URL_REGEX = "(?<=\\/)\\d+(?=\\/?)";
  private static String ID_REPLACEMENT = "{id}";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());


    router.post("/laa/ingest").handler(this::handleLogIngestion);
    router.get("/laa/simple").handler(this::handleMetrics);
    router.get("/laa/metrics/:groupBy").handler(this::handleComplexMetrics);

    vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(portCloud.orElse("8080")), res -> {
      if (res.succeeded()) {
        LOG.info("Server is now listening! ".concat("http://localhost:")
          .concat(String.valueOf(res.result().actualPort())
            .concat("/laa")));
        startPromise.complete();
      } else {
        LOG.error("Failed to bind!");
        startPromise.fail("Failed to bind");
      }
    });

  }
  private void handleComplexMetrics(RoutingContext routingContext) {
    SearchFilter searchFilter = this.parseAndBuildSearchFilter(routingContext);
    Map<String, Long> map = this.searchMetrics(searchFilter);
    if (map.isEmpty()) {
      routingContext.response()
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end(Json.encodePrettily(
          new ResponseModel(HttpResponseStatus.BAD_REQUEST.code(), "No metrics yet"))
        );
    } else {
      routingContext.response()
        .end(Json.encodePrettily(map));
    }

  }

  private Map<String, Long> searchMetrics(SearchFilter searchFilter ) {
    Map<String, Long> metrics = null;
    switch (searchFilter.getGroupBy()) {
      case REGION: {
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_REGION.name());
        break;
      }
      case URL:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_URL.name());
        break;
      }
      case MINUTE:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MINUTE.name());
        break;
      }
      case DAY:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_DAY.name());
        break;
      }
      case WEEK:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_WEEK.name());
        break;
      }
      case MONTH:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MONTH.name());
        break;
      }
      case YEAR:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_YEAR.name());
        break;
      }
    }
    return this.sortAndLimitSearchResults(metrics,
      searchFilter.getOrder(), searchFilter.getSize());
  }

  private Map<String, Long> sortAndLimitSearchResults(Map<String, Long> metrics, SearchOrder order, Integer size) {
    return metrics;
  }

  private SearchFilter parseAndBuildSearchFilter(RoutingContext routingContext) {
    HttpServerRequest request = routingContext.request();
    return SearchFilter.builder()
      .groupBy(SearchGroupBy.from(request.getParam("groupBy")))
      .order(SearchOrder.from(request.getParam("order")))
      /*
      .minute(getDateExpression(request.getParam("day"), DatePattern.DAY.getPattern()))
      .day(getDateExpression(request.getParam("day"), DatePattern.DAY.getPattern()))
      .month(getDateExpression(request.getParam("day"), DatePattern.DAY.getPattern()))
      .year(getDateExpression(request.getParam("day"), DatePattern.DAY.getPattern()))
      .week(getWeekExpression(request.getParam("week")))
       */
      .size(getIntegerParameter(request.getParam("size")))
      .createSearchFilter();
  }

  private String getDateExpression(String expression, String pattern) {
    if(expression == null || expression.trim().isBlank()) {
      return null;
    }
    try {
      new SimpleDateFormat(pattern).parse(expression);
    }
    catch (ParseException ex) {
      throw new ValidationException(ex.getMessage());
    }
    return expression;
  }

  private String getWeekExpression(String weekExpression) {
    Pattern pattern = Pattern.compile(VALID_WEEK_REGEX);
    Matcher matcher = pattern.matcher(weekExpression);
    if (matcher.find()) {
      int weekNumber = Integer.parseInt(matcher.group(1));
      if(weekNumber <= 0 || weekNumber > 55)
        throw new ValidationException("Invalid week format");
    }
    return weekExpression;
  }

  private Integer getIntegerParameter(String sizeEpression) {
    if(sizeEpression == null || sizeEpression.isBlank()) {
      return DEFAULT_SEARCH_SIZE;
    }
    try {
      return Integer.parseInt(sizeEpression.trim());
    }
    catch (Exception ex) {
      throw new ValidationException("Invalid value for size");
    }
  }

  private void handleMetrics(RoutingContext routingContext) {
    Map<?, ?> map = aggregateMetrics();
    if (map.isEmpty()) {
      routingContext.response()
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end(Json.encodePrettily(
          new ResponseModel(HttpResponseStatus.BAD_REQUEST.code(), "No metrics yet"))
        );
    } else {
      routingContext.response()
        .end(Json.encodePrettily(map));
    }
  }

  private Map<String, Map<String, String>> aggregateMetrics() {
    Map<String, Map<String, String>> map = new HashMap<>();
    map.put(MetricGroupType.GROUP_BY_URL.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_URL.name()));
    map.put(MetricGroupType.GROUP_BY_REGION.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_REGION.name()));
    map.put(MetricGroupType.GROUP_BY_DAY.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_DAY.name()));
    map.put(MetricGroupType.GROUP_BY_WEEK.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_WEEK.name()));
    map.put(MetricGroupType.GROUP_BY_MONTH.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MONTH.name()));
    map.put(MetricGroupType.GROUP_BY_YEAR.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_YEAR.name()));
    map.put(MetricGroupType.GROUP_BY_MINUTE.name(), vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MINUTE.name()));
    return map;
  }

  private void handleLogIngestion(RoutingContext routingContext) {
    String ingestedLog = routingContext.getBodyAsString();
    Optional<LogRequest> logRequest = this.parseLog(ingestedLog);
    logRequest.ifPresentOrElse(log -> {
      LOG.debug("Log parsed successfully: {}", log);
      try {
        vertx.eventBus().send(EventBusAddress.LOG_RECEIVED.name(), log);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      routingContext.response()
        .end(Json.encodePrettily(log));
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
    if (matcher.find()) {
      Optional<AwsRegion> region = AwsRegion.from(Integer.valueOf(matcher.group(4)));
      if (region.isPresent()) {
        return Optional.of(
          LogRequest
            .builder()
            .setUrl(matcher.group(1).replaceAll(REPLACE_IDS_IN_URL_REGEX, ID_REPLACEMENT))
            .setDate(Instant.ofEpochSecond(Long.parseLong(matcher.group(2))))
            .setClientId(matcher.group(3))
            .setRegion(region.get())
            .createLogRequest()
        );
      }
    }
    return Optional.empty();
  }
}
