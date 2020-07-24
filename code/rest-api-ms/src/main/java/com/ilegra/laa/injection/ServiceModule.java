package com.ilegra.laa.injection;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.ilegra.laa.service.MetricCacheService;
import com.ilegra.laa.service.MetricCacheServiceImpl;

public class ServiceModule implements Module {
  @Override
  public void configure(Binder binder) {
    binder.bind(MetricCacheService.class)
      .to(MetricCacheServiceImpl.class);
  }
}
