package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.GlobalMetricsWrapper;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Path("/laa/metrics-status")
public class SimpleMetricsRestController {
  protected final RedisClient redisClient;

  public SimpleMetricsRestController() {
    RedisOptions options = new RedisOptions().setHost("localhost").setPort(6379).setAuth("Illegra2020!").setSelect(1);
    this.redisClient = RedisClient.create(Vertx.vertx(), options);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response handleMetrics(@Context Vertx vertx) throws InterruptedException {
    GlobalMetricsWrapper metricsWrapper = aggregateMetrics(vertx);
    return Response.ok(metricsWrapper).build();
  }

  private GlobalMetricsWrapper aggregateMetrics(Vertx vertx) throws InterruptedException {
    return GlobalMetricsWrapper.builder()
      .rankingByUrl(this.find(MetricGroupType.GROUP_BY_URL.name(), RankingEntry[].class))
      .rankingByMinute(this.find(MetricGroupType.GROUP_BY_MINUTE.name(), RankingEntry[].class))
      .rankingByDay(this.find(MetricGroupType.GROUP_BY_DAY.name(), GroupedRankingEntry[].class))
      .rankingByWeek(this.find(MetricGroupType.GROUP_BY_WEEK.name(), GroupedRankingEntry[].class))
      .rankingByMonth(this.find(MetricGroupType.GROUP_BY_MONTH.name(), GroupedRankingEntry[].class))
      .rankingByYear(this.find(MetricGroupType.GROUP_BY_YEAR.name(), GroupedRankingEntry[].class))
      .rankingByRegion(this.find(MetricGroupType.GROUP_BY_REGION.name(), GroupedRankingEntry[].class))
    .build();
  }

  private <T> Set<T> find(String name, Class<T[]> arrayClass) throws InterruptedException {
    Set<T> result = new HashSet<>();
    final CountDownLatch latch = new CountDownLatch(1);
    this.redisClient.get(name, responseAsyncResult -> {
      if(responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
        T[] cachedEntities = Json.decodeValue(responseAsyncResult.result(),arrayClass);
        result.addAll(Arrays.asList(cachedEntities));
        latch.countDown();
      }
    });
    latch.await();
    return result;
  }

}
