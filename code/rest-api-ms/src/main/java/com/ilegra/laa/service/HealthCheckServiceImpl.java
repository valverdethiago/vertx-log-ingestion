package com.ilegra.laa.service;

import com.ilegra.laa.config.ServerSettings;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;

import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class HealthCheckServiceImpl implements HealthCheckService {

  private final Vertx vertx;
  private final ServerSettings settings;
  private final RedisOptions redisOptions;
  private HealthCheckHandler healthCheckHandler;

  @Inject
  public HealthCheckServiceImpl(Vertx vertx, ServerSettings settings, RedisOptions redisOptions) {
    this.vertx = vertx;
    this.settings = settings;
    this.redisOptions = redisOptions;
    this.healthCheckHandler = HealthCheckHandler.create(vertx);
  }


  @Override
  public HealthCheckHandler createHealthCheckHandler() {
    healthCheckHandler.register("redis-connection", promise -> {
      RedisClient.create(vertx, redisOptions).ping(responseAsyncResult -> {
        if(responseAsyncResult.succeeded())
          promise.tryComplete(Status.OK());
        else
          promise.tryFail(responseAsyncResult.cause());
      });
    });
    healthCheckHandler.register("kafka-connection", promise -> {
      KafkaConsumer.create(vertx, this.getKafkaConfiguration()).listTopics(responseAsyncResult -> {
        if(responseAsyncResult.succeeded())
          promise.tryComplete(Status.OK());
        else
          promise.tryFail(responseAsyncResult.cause());
      });
    });

    return healthCheckHandler;
  }

  private Properties getKafkaConfiguration() {
    final Properties config = new Properties();
    config.put("bootstrap.servers", settings.getKafkaServer());
    config.put("key.deserializer", StringDeserializer.class.getTypeName());
    config.put("value.deserializer", StringDeserializer.class.getTypeName());
    /*
    config.put("group.id", "log-access-analytics-consumer-"+this.metricGroupType.name());
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");
     */
    return config;
  }

}
