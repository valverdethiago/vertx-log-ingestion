package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogAggregator;
import io.vertx.core.json.Json;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class LogAggregatorDeserializer implements Deserializer<LogAggregator> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public LogAggregator deserialize(String topic, byte[] data) {
    if(data == null) {
      return null;
    }
    return Json.decodeValue(new String(data), LogAggregator.class);
  }

  @Override
  public LogAggregator deserialize(String topic, Headers headers, byte[] data) {
    return this.deserialize(topic, data);
  }

  @Override
  public void close() {

  }
}
