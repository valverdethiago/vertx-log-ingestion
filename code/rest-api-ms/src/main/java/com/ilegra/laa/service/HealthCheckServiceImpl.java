package com.ilegra.laa.service;

import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.apache.kafka.streams.KafkaStreams;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class HealthCheckServiceImpl implements HealthCheckService {

  private final Vertx vertx;
  private final RedisOptions redisOptions;
  private List<KafkaStreams> kafkaStreamsList;

  @Inject
  public HealthCheckServiceImpl(Vertx vertx, RedisOptions redisOptions) {
    this.vertx = vertx;
    this.redisOptions = redisOptions;
    this.kafkaStreamsList = new ArrayList<>();
  }


  @Override
  public HealthCheckHandler createHealthCheckHandler() {
    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
    healthCheckHandler.register("redis-connection", 20, promise -> {
      RedisClient.create(vertx, redisOptions).ping(responseAsyncResult -> {
        promise.complete(responseAsyncResult.succeeded() ? Status.OK() : Status.KO());
      });
    });
    return healthCheckHandler;
  }
}
