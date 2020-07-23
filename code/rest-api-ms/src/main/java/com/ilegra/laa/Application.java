package com.ilegra.laa;

import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.vertx.codecs.GroupedRankingEntryCodec;
import com.ilegra.laa.vertx.codecs.LogEntryCodec;
import com.ilegra.laa.vertx.codecs.RankingEntryCodec;
import com.ilegra.laa.vertx.verticles.*;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    vertx.eventBus().registerDefaultCodec(LogEntry.class, new LogEntryCodec());
    vertx.eventBus().registerDefaultCodec(RankingEntry.class, new RankingEntryCodec());
    vertx.eventBus().registerDefaultCodec(GroupedRankingEntry.class, new GroupedRankingEntryCodec());
    vertx.deployVerticle(new HttpServerVerticle());
    vertx.deployVerticle(new LogProducerVerticle());
    vertx.deployVerticle(new MetricUpdateEventListenerVerticle());
    vertx.deployVerticle(new GroupedMetricUpdateEventListenerVerticle());
    vertx.deployVerticle(new LogAggregatorByUrlVerticle());
    vertx.deployVerticle(new LogAggregatorByMinuteVerticle());
    vertx.deployVerticle(new LogAggregatorByDayVerticle());
    vertx.deployVerticle(new LogAggregatorByRegionVerticle());
    vertx.deployVerticle(new LogAggregatorByWeekVerticle());
    vertx.deployVerticle(new LogAggregatorByMonthVerticle());
    vertx.deployVerticle(new LogAggregatorByYearVerticle());
  }
}
