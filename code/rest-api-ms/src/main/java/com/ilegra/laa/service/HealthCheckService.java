package com.ilegra.laa.service;

import io.vertx.ext.healthchecks.HealthCheckHandler;
import org.apache.kafka.streams.KafkaStreams;

public interface HealthCheckService {

  HealthCheckHandler createHealthCheckHandler();
  void registerHealthCheckHandler(String name, KafkaStreams kafkaStreams);
}
