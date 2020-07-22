package com.ilegra.laa;

import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.vertx.codecs.LogRequestCodec;
import com.ilegra.laa.vertx.verticles.*;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    vertx.eventBus().registerDefaultCodec(LogEntry.class, new LogRequestCodec());
    vertx.deployVerticle(new HttpServerVerticle());
    vertx.deployVerticle(new LogProducerVerticle());
    vertx.deployVerticle(new LogAggregatorByUrlVerticle());
    vertx.deployVerticle(new LogAggregatorByRegionVerticle());
    vertx.deployVerticle(new LogAggregatorByDayVerticle());
    vertx.deployVerticle(new LogAggregatorByWeekVerticle());
    vertx.deployVerticle(new LogAggregatorByMonthVerticle());
    vertx.deployVerticle(new LogAggregatorByYearVerticle());
    vertx.deployVerticle(new LogAggregatorByMinuteVerticle());
  }
}
