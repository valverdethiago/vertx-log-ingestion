package com.ilegra.laa.service;

import io.vertx.ext.healthchecks.HealthCheckHandler;

public interface HealthCheckService {

  public HealthCheckHandler createHealthCheckHandler();
}
