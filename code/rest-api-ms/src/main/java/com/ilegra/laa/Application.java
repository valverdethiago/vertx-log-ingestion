package com.ilegra.laa;

import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.verticles.HttpServerVerticle;
import com.ilegra.laa.verticles.LogAggregatorByUrlVerticle;
import com.ilegra.laa.verticles.LogProducerVerticle;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    vertx.eventBus().registerDefaultCodec(LogRequest.class, new LogRequestCodec());
    vertx.deployVerticle(new HttpServerVerticle());
    vertx.deployVerticle(new LogProducerVerticle());
    vertx.deployVerticle(new LogAggregatorByUrlVerticle());
  }
}
