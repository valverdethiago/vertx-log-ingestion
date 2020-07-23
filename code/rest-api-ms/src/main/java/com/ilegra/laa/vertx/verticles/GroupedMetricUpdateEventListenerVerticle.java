package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.redis.RedisOptions;

import java.util.*;

public class GroupedMetricUpdateEventListenerVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(GroupedMetricUpdateEventListenerVerticle.class);

  private RedisClient redisClient;

  @Override
  public void start(final Promise<Void> startPromise) {
    this.startRedisClient(startPromise);
    this.startEventConsumers();
    startPromise.complete();
  }

  private void startEventConsumers() {
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_YEAR.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_YEAR, message));
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_MONTH.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_MONTH, message));
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_WEEK.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_WEEK, message));
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_DAY.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_DAY, message));
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_REGION.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_REGION, message));
  }

  private void startRedisClient(Promise<Void> startPromise) {
    RedisOptions options = new RedisOptions().setHost("localhost").setPort(6379).setAuth("Illegra2020!").setSelect(1);
    this.redisClient = RedisClient.create(vertx, options);
  }

  private void consumeMessage(MetricGroupType metricGroupType, Message<?> message) {
    final GroupedRankingEntry entry = (GroupedRankingEntry) message.body();
    final Set<GroupedRankingEntry> rankingToBeCached = new HashSet<>();
    rankingToBeCached.add(entry);
    this.redisClient.get(metricGroupType.name(), responseAsyncResult -> {
      if(responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
        GroupedRankingEntry[] currentRankingEntries = Json.decodeValue(responseAsyncResult.result(),
          GroupedRankingEntry[].class);
        rankingToBeCached.addAll(Arrays.asList(currentRankingEntries));
      }
      this.redisClient.set(metricGroupType.name(), Json.encodePrettily(rankingToBeCached), this::handleRedisUpdate);
    });
  }

  private void handleRedisUpdate(AsyncResult<Void> voidAsyncResult) {
    if(voidAsyncResult.succeeded()) {
      LOG.debug("Metric update saved successfully on Redis ");
    }
    else {
      LOG.debug("Error saving the metric update on Redis ");
    }
  }

}
