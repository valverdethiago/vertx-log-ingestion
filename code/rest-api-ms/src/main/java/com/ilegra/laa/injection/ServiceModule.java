package com.ilegra.laa.injection;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.ilegra.laa.service.MetricCacheService;
import com.ilegra.laa.service.MetricCacheServiceImpl;
import com.ilegra.laa.config.ServerSettings;

public class ServiceModule implements Module {

  private final ServerSettings settings;

  public ServiceModule(ServerSettings settings) {
    this.settings = settings;
  }

  @Override
  public void configure(Binder binder) {
    binder.bind(MetricCacheService.class)
      .to(MetricCacheServiceImpl.class);
    binder.bind(ServerSettings.class).toInstance(settings);
  }
}
