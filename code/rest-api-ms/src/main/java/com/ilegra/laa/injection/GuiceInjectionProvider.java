package com.ilegra.laa.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zandero.rest.injection.InjectionProvider;
import com.ilegra.laa.config.ServerSettings;
import io.vertx.core.Vertx;

public class GuiceInjectionProvider implements InjectionProvider {
  private Injector injector;

  public GuiceInjectionProvider(Vertx vertx, ServerSettings settings) {
    injector = Guice.createInjector(new ServiceModule(vertx, settings));

  }

  @Override
  public <T> T getInstance(Class<T> clazz) throws Throwable {
    return injector.getInstance(clazz);
  }
}
