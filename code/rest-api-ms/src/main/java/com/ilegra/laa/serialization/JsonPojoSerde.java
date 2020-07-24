package com.ilegra.laa.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;

public class JsonPojoSerde<T extends Serializable> implements Serde<T> {

  private final Class<T> pojoClass;

  public JsonPojoSerde(Class<T> pojoClass) {
    this.pojoClass = pojoClass;
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public void close() {
  }

  @Override
  public Serializer<T> serializer() {
    return new JsonPojoSerializer<>();
  }

  @Override
  public Deserializer<T> deserializer() {
    return new JsonPojoDeserializer<>(pojoClass);
  }
}
