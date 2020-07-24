package com.ilegra.laa.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
