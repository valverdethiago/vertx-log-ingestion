package com.ilegra.laa.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zandero.rest.injection.InjectionProvider;

public class GuiceInjectionProvider implements InjectionProvider {
  private Injector injector;

  public GuiceInjectionProvider() {
    injector = Guice.createInjector(new ServiceModule());
  }

  @Override
  public <T> T getInstance(Class<T> clazz) throws Throwable {
    return injector.getInstance(clazz);
  }
}
