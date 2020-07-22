package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogAggregator;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class LogAggregatorSerde implements Serde<LogAggregator> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public void close() {
  }

  @Override
  public Serializer<LogAggregator> serializer() {
    return new LogAggregatorSerializer();
  }

  @Override
  public Deserializer<LogAggregator> deserializer() {
    return new LogAggregatorDeserializer();
  }
}
