package com.ilegra.laa.injection;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.ilegra.laa.config.ServerSettings;
import com.ilegra.laa.service.MetricCacheService;
import com.ilegra.laa.service.MetricCacheServiceImpl;
import io.vertx.core.Vertx;

public class ServiceModule implements Module {

  private final ServerSettings settings;
  private final Vertx vertx;

  public ServiceModule(Vertx vertx, ServerSettings settings) {
    this.vertx = vertx;
    this.settings = settings;
  }

  @Override
  public void configure(Binder binder) {
    binder.bind(MetricCacheService.class)
      .to(MetricCacheServiceImpl.class);
    binder.bind(ServerSettings.class)
      .toInstance(settings);
    binder.bind(Vertx.class)
      .toInstance(vertx);

  }
}
