package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogEntry;

public class LogEntrySerde extends JsonPojoSerde<LogEntry>{
  public LogEntrySerde() {
    super(LogEntry.class);
  }
}
