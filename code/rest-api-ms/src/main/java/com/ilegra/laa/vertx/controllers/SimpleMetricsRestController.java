package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.GlobalMetricsWrapper;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.service.MetricCacheService;
import io.vertx.core.Vertx;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/laa/metrics-status")
public class SimpleMetricsRestController {
  @Inject
  private MetricCacheService metricCacheService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response handleMetrics(@Context Vertx vertx) throws InterruptedException {
    GlobalMetricsWrapper metricsWrapper = aggregateMetrics(vertx);
    return Response.ok(metricsWrapper).build();
  }

  private GlobalMetricsWrapper aggregateMetrics(Vertx vertx) throws InterruptedException {
    return GlobalMetricsWrapper.builder()
      .rankingByUrl(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_URL, RankingEntry[].class))
      .rankingByMinute(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_MINUTE, RankingEntry[].class))
      .rankingByDay(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_DAY, GroupedRankingEntry[].class))
      .rankingByWeek(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_WEEK, GroupedRankingEntry[].class))
      .rankingByMonth(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_MONTH, GroupedRankingEntry[].class))
      .rankingByYear(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_YEAR, GroupedRankingEntry[].class))
      .rankingByRegion(this.metricCacheService.getMetrics(MetricGroupType.GROUP_BY_REGION, GroupedRankingEntry[].class))
    .build();
  }

}
