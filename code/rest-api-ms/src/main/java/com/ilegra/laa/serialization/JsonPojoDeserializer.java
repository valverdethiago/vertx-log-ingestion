package com.ilegra.laa.serialization;

import io.vertx.core.json.Json;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.Serializable;
import java.util.Map;

/**
 * Generic Json Deserializer
 *
 * @param <T> Any entity that is @{@link Serializable}
 *
 * @author valverde.thiago
 */
public class JsonPojoDeserializer<T extends Serializable> implements Deserializer<T> {

  private final Class<T> pojoClass;

  public JsonPojoDeserializer(Class<T> pojoClass) {
    this.pojoClass = pojoClass;
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public T deserialize(String topic, byte[] data) {
    if(data == null) {
      return null;
    }
    return Json.decodeValue(new String(data), pojoClass);
  }

  @Override
  public T deserialize(String topic, Headers headers, byte[] data) {
    return this.deserialize(topic, data);
  }

  @Override
  public void close() {

  }
}
