package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.LogEntry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class LogRequestCodec implements MessageCodec<LogEntry, LogEntry> {
  @Override
  public void encodeToWire(Buffer buffer, LogEntry logEntry) {
    String json = Json.encodePrettily(logEntry);
    int length = json.getBytes().length;
    buffer.appendInt(length);
    buffer.appendString(json);
  }

  @Override
  public LogEntry decodeFromWire(int position, Buffer buffer) {
    return Json.decodeValue(buffer, LogEntry.class);
  }

  @Override
  public LogEntry transform(LogEntry logEntry) {
    return logEntry;
  }

  @Override
  public String name() {
    return LogRequestCodec.class.getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
