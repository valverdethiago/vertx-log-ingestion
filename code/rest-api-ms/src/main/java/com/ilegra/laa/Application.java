package com.ilegra.laa;

import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.vertx.verticles.HttpServerVerticle;
import com.ilegra.laa.vertx.verticles.LogAggregatorByRegionVerticle;
import com.ilegra.laa.vertx.verticles.LogAggregatorByUrlVerticle;
import com.ilegra.laa.vertx.verticles.LogProducerVerticle;
import com.ilegra.laa.vertx.codecs.LogRequestCodec;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    vertx.eventBus().registerDefaultCodec(LogRequest.class, new LogRequestCodec());
    vertx.deployVerticle(new HttpServerVerticle());
    vertx.deployVerticle(new LogProducerVerticle());
    vertx.deployVerticle(new LogAggregatorByUrlVerticle());
    vertx.deployVerticle(new LogAggregatorByRegionVerticle());
  }
}
