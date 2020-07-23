package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.redis.client.RedisAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MetricUpdateEventListenerVerticle extends AbstractRedisVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(MetricUpdateEventListenerVerticle.class);


  @Override
  public void start(final Promise<Void> startPromise) {
    this.startRedisClient(startPromise);
    this.startEventConsumers();
    startPromise.complete();
  }

  private void startEventConsumers() {
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_MINUTE.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_MINUTE, message));
    vertx.eventBus().localConsumer(MetricGroupType.GROUP_BY_URL.name(),
      (message)->this.consumeMessage(MetricGroupType.GROUP_BY_URL, message));
  }

  private void consumeMessage(MetricGroupType metricGroupType, Message<?> message) {
    final RankingEntry entry = (RankingEntry) message.body();
    final Set<RankingEntry> rankingToBeCached = new HashSet<>();
    rankingToBeCached.add(entry);
    this.redisClient.get(metricGroupType.name(), responseAsyncResult -> {
      if(responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
        RankingEntry[] currentRankingEntries = Json.decodeValue(responseAsyncResult.result(), RankingEntry[].class);
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
