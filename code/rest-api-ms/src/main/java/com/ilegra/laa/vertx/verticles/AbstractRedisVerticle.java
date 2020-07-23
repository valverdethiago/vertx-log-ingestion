package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AbstractRedisVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(AbstractRedisVerticle.class);

  protected RedisClient redisClient;

  public void startRedisClient(final Promise<Void> startPromise) {
    RedisOptions options = new RedisOptions()
      .setHost("localhost")
      .setPort(6379)
      .setAuth("Illegra2020!").setSelect(1);
    this.redisClient = RedisClient.create(vertx, options);
  }


}
