package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.models.search.GlobalMetricsWrapper;
import com.ilegra.laa.service.MetricCacheService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.ilegra.laa.vertx.verticles.HttpServerVerticle.API_PATH;

/**
 * Internal controller to check all metrics without specifying a filter
 *
 * @author valverde.thiago
 */
@Path(API_PATH+"/metrics-status")
public class SimpleMetricsRestController {
  @Inject
  private MetricCacheService metricCacheService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response handleMetrics()  {
    GlobalMetricsWrapper metricsWrapper = aggregateMetrics();
    return Response.ok(metricsWrapper).build();
  }

  private GlobalMetricsWrapper aggregateMetrics() {
    return GlobalMetricsWrapper.builder()
      .rankingByUrl(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_URL, RankingEntry[].class)
      )
      .rankingByMinute(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_MINUTE, RankingEntry[].class)
      )
      .rankingByDay(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_DAY, GroupedRankingEntry[].class)
      )
      .rankingByWeek(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_WEEK, GroupedRankingEntry[].class)
      )
      .rankingByMonth(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_MONTH, GroupedRankingEntry[].class)
      )
      .rankingByYear(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_YEAR, GroupedRankingEntry[].class)
      )
      .rankingByRegion(
        this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_REGION, GroupedRankingEntry[].class)
      )
    .build();
  }

}
