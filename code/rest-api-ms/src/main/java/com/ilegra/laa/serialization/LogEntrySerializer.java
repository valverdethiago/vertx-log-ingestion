package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogEntry;
import io.vertx.core.json.Json;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class LogEntrySerializer implements Serializer<LogEntry> {
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public byte[] serialize(String topic, LogEntry logEntry) {
    if(logEntry == null) {
      return null;
    }
    return Json.encodePrettily(logEntry).getBytes();
  }

  @Override
  public byte[] serialize(String topic, Headers headers, LogEntry logEntry) {
    return this.serialize(topic, logEntry);
  }

  @Override
  public void close() {
  }
}
