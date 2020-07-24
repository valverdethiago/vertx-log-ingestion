package com.ilegra.laa.service;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.apache.kafka.streams.KafkaStreams;

import javax.inject.Inject;

public class HealthCheckServiceImpl implements HealthCheckService {

  private final Vertx vertx;
  private final RedisOptions redisOptions;
  private HealthCheckHandler healthCheckHandler;

  @Inject
  public HealthCheckServiceImpl(Vertx vertx, RedisOptions redisOptions) {
    this.vertx = vertx;
    this.redisOptions = redisOptions;
    this.healthCheckHandler = HealthCheckHandler.create(vertx);
  }


  @Override
  public HealthCheckHandler createHealthCheckHandler() {
    healthCheckHandler.register("redis-connection", 20, promise -> {
      RedisClient.create(vertx, redisOptions).ping(responseAsyncResult -> {
        promise.complete(responseAsyncResult.succeeded() ? Status.OK() : Status.KO());
      });
    });

    return healthCheckHandler;
  }

  public void registerHealthCheckHandler(String name, KafkaStreams kafkaStreams) {
    healthCheckHandler.register(name, promise -> {
      promise.complete(kafkaStreams.state() == KafkaStreams.State.RUNNING ? Status.OK() : Status.KO());
    });
  }

}
