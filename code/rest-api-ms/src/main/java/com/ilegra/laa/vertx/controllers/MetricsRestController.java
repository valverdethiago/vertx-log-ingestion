package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.*;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/laa/metrics")
public class MetricsRestController {

  private static final Integer DEFAULT_SEARCH_SIZE = 3;
  private static String VALID_WEEK_REGEX = "(\\d{1,2})\\-(\\d{1,4})";

  @Path("/{groupBy}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchMetrics(@Context Vertx vertx,
                                        @PathParam("groupBy") String groupBy,
                                        @Context HttpServerRequest request) {
    SearchFilter searchFilter = this.parseAndBuildSearchFilter(request, groupBy);
    Map<String, String> map = this.searchMetrics(vertx, searchFilter);
    if (map.isEmpty()) {
      return Response.noContent().build();
    } else {
      return Response.ok(map).build();
    }

  }

  private Map<String, String> searchMetrics(Vertx vertx, SearchFilter searchFilter ) {
    Map<String, String> metrics = null;
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
        searchFilter.setSearchTerm(searchFilter.getMinute());
        break;
      }
      case DAY:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_DAY.name());
        searchFilter.setSearchTerm(searchFilter.getDay());
        break;
      }
      case WEEK:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_WEEK.name());
        searchFilter.setSearchTerm(searchFilter.getWeek());
        break;
      }
      case MONTH:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MONTH.name());
        searchFilter.setSearchTerm(searchFilter.getMonth());
        break;
      }
      case YEAR:{
        metrics = vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_YEAR.name());
        searchFilter.setSearchTerm(searchFilter.getYear());
        break;
      }
    }
    return this.sortAndLimitSearchResults(metrics,
      searchFilter);
  }

  private Map<String, String> sortAndLimitSearchResults(Map<String, String> metrics, SearchFilter searchFilter) {
    if(searchFilter.getSearchTerm() == null) {
      return metrics;
    }

    return metrics.entrySet().stream()
      .filter(entry -> entry.getKey().equals(searchFilter.getSearchTerm()))
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
  }

  private SearchFilter parseAndBuildSearchFilter(HttpServerRequest request, String groupBy) {
    return SearchFilter.builder()
      .groupBy(SearchGroupBy.from(groupBy))
      .order(SearchOrder.from(request.getParam("order")))
      .minute(getDateExpression(request.getParam("minute"), DatePattern.MINUTE.getPattern()))
      .day(getDateExpression(request.getParam("day"), DatePattern.DAY.getPattern()))
      .month(getDateExpression(request.getParam("month"), DatePattern.MONTH.getPattern()))
      .year(getDateExpression(request.getParam("year"), DatePattern.YEAR.getPattern()))
      .week(getWeekExpression(request.getParam("week")))
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
    if(weekExpression == null || weekExpression.isBlank()) {
      return null;
    }
    Pattern pattern = Pattern.compile(VALID_WEEK_REGEX);
    Matcher matcher = pattern.matcher(weekExpression);
    if (matcher.find()) {
      int weekNumber = Integer.parseInt(matcher.group(1));
      if(weekNumber <= 0 || weekNumber > 55)
        throw new ValidationException("Invalid week format");
    }
    return weekExpression;
  }

  private Integer getIntegerParameter(String sizeExpression) {
    if(sizeExpression == null || sizeExpression.isBlank()) {
      return DEFAULT_SEARCH_SIZE;
    }
    try {
      return Integer.parseInt(sizeExpression.trim());
    }
    catch (Exception ex) {
      throw new ValidationException("Invalid value for size");
    }
  }
}
