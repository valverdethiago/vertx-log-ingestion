package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.LogEntry;

public class LogEntryCodec extends JsonPojoCodec<LogEntry> {
  public LogEntryCodec() {
    super(LogEntry.class);
  }
}
