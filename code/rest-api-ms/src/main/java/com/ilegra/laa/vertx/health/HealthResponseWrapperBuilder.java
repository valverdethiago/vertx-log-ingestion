package com.ilegra.laa.vertx.health;

import java.util.Map;

public class HealthResponseWrapperBuilder {
    private Map<String, ComponentState> kafkaStreamStateMap;
    private ComponentState redisConnectionState;

    public HealthResponseWrapperBuilder kafkaStreamStateMap(Map<String, ComponentState> kafkaStreamStateMap) {
        this.kafkaStreamStateMap = kafkaStreamStateMap;
        return this;
    }

    public HealthResponseWrapperBuilder redisConnectionState(ComponentState redisConnectionState) {
        this.redisConnectionState = redisConnectionState;
        return this;
    }

    public HealthResponseWrapper build() {
        return new HealthResponseWrapper(kafkaStreamStateMap, redisConnectionState);
    }
}
