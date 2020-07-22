package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogEntry;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class LogEntrySerde implements Serde<LogEntry> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public void close() {
  }

  @Override
  public Serializer<LogEntry> serializer() {
    return new LogEntrySerializer();
  }

  @Override
  public Deserializer<LogEntry> deserializer() {
    return new LogEntryDeserializer();
  }
}
