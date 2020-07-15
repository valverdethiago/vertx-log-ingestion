package com.ilegra.laa;

import com.ilegra.laa.verticles.HttpServerVerticle;
import com.ilegra.laa.verticles.LogProducerVerticle;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HttpServerVerticle());
    vertx.deployVerticle(new LogProducerVerticle());
  }
}
