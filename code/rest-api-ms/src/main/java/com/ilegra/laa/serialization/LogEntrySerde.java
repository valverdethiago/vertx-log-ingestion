package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogEntry;

/**
 * Serde for processing @{@link LogEntry} json messages to and from kafka topics
 *
 * @author valverde.thiago
 */
public class LogEntrySerde extends JsonPojoSerde<LogEntry>{
  public LogEntrySerde() {
    super(LogEntry.class);
  }
}
