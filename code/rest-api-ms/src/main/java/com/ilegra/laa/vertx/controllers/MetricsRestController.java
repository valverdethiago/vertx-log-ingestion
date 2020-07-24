package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.*;
import com.ilegra.laa.models.exceptions.ValidationException;
import com.ilegra.laa.models.search.MetricResponseWrapper;
import com.ilegra.laa.models.search.SearchFilter;
import com.ilegra.laa.models.search.SearchOrder;
import com.ilegra.laa.models.search.SearchType;
import com.ilegra.laa.service.MetricCacheService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ilegra.laa.vertx.verticles.HttpServerVerticle.API_PATH;

@Path(API_PATH+"/metrics")
public class MetricsRestController {

  private static final Long DEFAULT_SEARCH_SIZE = 3L;
  private static String VALID_WEEK_REGEX = "(\\d{1,4})-(\\d{1,2})";

  @Inject
  private MetricCacheService metricCacheService;

  @Path("/{type}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchMetrics(@Context Vertx vertx,
                                @PathParam("type") String type,
                                @Context HttpServerRequest request) {
    SearchFilter searchFilter = this.parseAndBuildSearchFilter(request, type);
    MetricResponseWrapper searchResult = this.metricCacheService.searchMetrics(searchFilter);
    if (searchResult.isEmpty()) {
      return Response.noContent().build();
    } else {
      return Response.ok(searchResult).build();
    }
  }

  private SearchFilter parseAndBuildSearchFilter(HttpServerRequest request, String type) {
    SearchType searchType = SearchType.from(type);
    SearchOrder searchOrder = SearchOrder.from(request.getParam("order"));
    Long size = getLongParameter(request.getParam("size"));
    SearchFilter filter = SearchFilter.builder()
      .type(searchType == null ? SearchType.URL : searchType)
      .order(searchOrder == null ? SearchOrder.TOP : searchOrder)
      .minute(getDateExpression(request.getParam("minute"), DatePattern.MINUTE.getPattern()))
      .day(getDateExpression(request.getParam("day"), DatePattern.DAY.getPattern()))
      .month(getDateExpression(request.getParam("month"), DatePattern.MONTH.getPattern()))
      .year(getDateExpression(request.getParam("year"), DatePattern.YEAR.getPattern()))
      .week(getWeekExpression(request.getParam("week")))
      .size(size == null ? DEFAULT_SEARCH_SIZE : size)
      .build();
    int countDateFilters = 0;
    if (filter.getYear() != null && !filter.getYear().isBlank())
      countDateFilters++;
    if (filter.getMonth() != null && !filter.getMonth().isBlank())
      countDateFilters++;
    if (filter.getWeek() != null && !filter.getWeek().isBlank())
      countDateFilters++;
    if (filter.getDay() != null && !filter.getDay().isBlank())
      countDateFilters++;

    if(filter.getType() == SearchType.DATE && countDateFilters > 1) {
      throw new ValidationException("When searching by dates only one date filter is allowed");
    }
    if(filter.getType() == SearchType.DATE && countDateFilters == 0) {
      throw new ValidationException("When searching by dates at least one date filter is required");
    }
    if(filter.getType() != SearchType.DATE && countDateFilters > 0) {
      throw new ValidationException("Date filters are allowed only on search type date");
    }
    return filter;

  }

  private String getDateExpression(String expression, String pattern) {
    if (expression == null || expression.trim().isBlank()) {
      return null;
    }
    try {
      new SimpleDateFormat(pattern).parse(expression);
    } catch (ParseException ex) {
      throw new ValidationException(ex.getMessage());
    }
    return expression;
  }

  private String getWeekExpression(String weekExpression) {
    if (weekExpression == null || weekExpression.isBlank()) {
      return null;
    }
    Pattern pattern = Pattern.compile(VALID_WEEK_REGEX);
    Matcher matcher = pattern.matcher(weekExpression);
    if (matcher.find()) {
      int weekNumber = Integer.parseInt(matcher.group(2));
      if (weekNumber <= 0 || weekNumber > 55)
        throw new ValidationException("Invalid week format");
    }
    return weekExpression;
  }

  private Long getLongParameter(String sizeExpression) {
    if (sizeExpression == null || sizeExpression.isBlank()) {
      return DEFAULT_SEARCH_SIZE;
    }
    try {
      return Long.parseLong(sizeExpression.trim());
    } catch (Exception ex) {
      throw new ValidationException("Invalid value for size");
    }
  }
}
