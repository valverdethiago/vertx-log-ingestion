package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.ranking.RankingEntry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

import java.io.Serializable;

public class JsonPojoCodec<T extends Serializable> implements MessageCodec<T, T> {

  private final Class<T> clazz;

  public JsonPojoCodec(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public void encodeToWire(Buffer buffer, T pojo) {
    String json = Json.encodePrettily(pojo);
    int length = json.getBytes().length;
    buffer.appendInt(length);
    buffer.appendString(json);
  }

  @Override
  public T decodeFromWire(int position, Buffer buffer) {
    return Json.decodeValue(buffer, this.clazz);
  }

  @Override
  public T transform(T pojo) {
    return pojo;
  }

  @Override
  public String name() {
    return this.getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
