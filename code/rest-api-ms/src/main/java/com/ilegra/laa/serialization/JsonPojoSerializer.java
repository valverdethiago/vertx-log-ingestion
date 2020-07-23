package com.ilegra.laa.serialization;

import io.vertx.core.json.Json;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;

public class JsonPojoSerializer<T extends Serializable> implements Serializer<T> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public byte[] serialize(String topic, T pojo) {
    if(pojo == null) {
      return null;
    }
    return Json.encodePrettily(pojo).getBytes();
  }

  @Override
  public byte[] serialize(String topic, Headers headers, T pojo) {
    return this.serialize(topic, pojo);
  }

  @Override
  public void close() {
  }
}
