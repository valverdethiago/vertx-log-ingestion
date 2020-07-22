package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogAggregator;
import io.vertx.core.json.Json;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class LogAggregatorSerializer implements Serializer<LogAggregator> {
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public byte[] serialize(String topic, LogAggregator logAggregator) {
    if(logAggregator == null) {
      return null;
    }
    return Json.encodePrettily(logAggregator).getBytes();
  }

  @Override
  public byte[] serialize(String topic, Headers headers, LogAggregator logAggregator) {
    return this.serialize(topic, logAggregator);
  }

  @Override
  public void close() {
  }
}
