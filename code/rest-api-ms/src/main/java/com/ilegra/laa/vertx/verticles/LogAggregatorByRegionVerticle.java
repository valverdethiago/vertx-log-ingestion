package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;

public class LogAggregatorByRegionVerticle extends AbstractLogAggregatorVerticle {

  public LogAggregatorByRegionVerticle() {
    super(MetricGroupType.GROUP_BY_REGION,
      KafkaTopic.LOGS_INPUT,
      KafkaTopic.LOGS_GROUP_BY_REGION_OUTPUT);
  }

  @Override
  protected String groupBy(LogEntry logRequest) {
    return logRequest.getRegion().getName();
  }
}
