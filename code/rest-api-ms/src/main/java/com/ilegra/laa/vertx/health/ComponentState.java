package com.ilegra.laa.vertx.health;

import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;

public class ComponentState {

  public static final String COMPONENTS_STATE_MAP = "COMPONENTS_STATE_MAP";

  private Instant since;
  private boolean working;

  public ComponentState() {
  }

  private ComponentState(Instant since, Boolean working) {
    this.since = since;
    this.working = working;
  }

  public static ComponentState off() {
    return createComponentState(Instant.now(), false);
  }

  public static ComponentState on() {
    return createComponentState(Instant.now(), true);
  }

  public static ComponentState createComponentState(Instant since, Boolean working) {
    return new ComponentState(since, working);
  }

  public Instant getSince() {
    return since;
  }

  public boolean isWorking() {
    return working;
  }

  public void setWorking(boolean working) {
    this.working = working;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ComponentState.class.getSimpleName() + "[", "]")
      .add("since=" + since)
      .add("working=" + working)
      .toString();
  }
}
