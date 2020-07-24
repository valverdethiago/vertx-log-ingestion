package com.ilegra.laa.vertx.health;

import io.vertx.core.json.Json;
import io.vertx.core.shareddata.SharedData;
import org.apache.kafka.streams.KafkaStreams;

import static com.ilegra.laa.vertx.health.ComponentState.*;

public class KafkaStateListener implements KafkaStreams.StateListener {

  private final String name;
  private final SharedData sharedData;


  public KafkaStateListener(String name, SharedData sharedData) {
    this.name = name;
    this.sharedData = sharedData;
  }

  @Override
  public void onChange(KafkaStreams.State oldState, KafkaStreams.State newState) {
    boolean isResourceRunning = isRunning(newState);
    this.sharedData.getLocalMap(COMPONENTS_STATE_MAP).put(name, Json.encode(isResourceRunning ? on() : off()));
  }

  private boolean isRunning(KafkaStreams.State state) {
    return state == KafkaStreams.State.CREATED
      || state == KafkaStreams.State.REBALANCING
      || state == KafkaStreams.State.REBALANCING;
  }
}
