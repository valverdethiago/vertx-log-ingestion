package com.ilegra.laa.vertx.health;

import java.util.Map;

public class HealthResponseWrapper {

  private Map<String, ComponentState> kafkaStreamStateMap;
  private ComponentState redisConnectionState;

  public HealthResponseWrapper() {
  }

  public HealthResponseWrapper(Map<String, ComponentState> kafkaStreamStateMap, ComponentState redisConnectionState) {
    this.kafkaStreamStateMap = kafkaStreamStateMap;
    this.redisConnectionState = redisConnectionState;
  }

  public static HealthResponseWrapperBuilder builder() {
    return new HealthResponseWrapperBuilder();
  }

  public Map<String, ComponentState> getKafkaStreamStateMap() {
    return kafkaStreamStateMap;
  }

  public ComponentState getRedisConnectionState() {
    return redisConnectionState;
  }
}
