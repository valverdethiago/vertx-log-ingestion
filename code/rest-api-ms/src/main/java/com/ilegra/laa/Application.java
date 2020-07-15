package com.ilegra.laa;

import com.ilegra.laa.verticles.HttpServerVerticle;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HttpServerVerticle());
  }
}
