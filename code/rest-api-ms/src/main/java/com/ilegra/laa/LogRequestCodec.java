package com.ilegra.laa;

import com.ilegra.laa.models.LogRequest;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class LogRequestCodec implements MessageCodec<LogRequest, LogRequest> {
  @Override
  public void encodeToWire(Buffer buffer, LogRequest logRequest) {
    String json = Json.encodePrettily(logRequest);
    int length = json.getBytes().length;
    buffer.appendInt(length);
    buffer.appendString(json);
  }

  @Override
  public LogRequest decodeFromWire(int position, Buffer buffer) {
    return Json.decodeValue(buffer, LogRequest.class);
  }

  @Override
  public LogRequest transform(LogRequest logRequest) {
    return logRequest;
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
