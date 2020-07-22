package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogEntry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class LogEntryTimestampExtractor implements TimestampExtractor {
  @Override
  public long extract(ConsumerRecord<Object, Object> consumerRecord, long l) {
    if (consumerRecord != null && consumerRecord.value() != null) {
      if (consumerRecord.value() instanceof LogEntry) {
        LogEntry value = (LogEntry) consumerRecord.value();
        return value.getDate().getEpochSecond();
      }
    }
    return consumerRecord.timestamp();
  }
}
