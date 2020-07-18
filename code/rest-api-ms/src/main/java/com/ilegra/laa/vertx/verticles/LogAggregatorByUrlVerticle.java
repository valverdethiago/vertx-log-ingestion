package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricGroupType;

public class LogAggregatorByUrlVerticle extends AbstractLogAggregatorVerticle {

  public LogAggregatorByUrlVerticle() {
    super(MetricGroupType.GROUP_BY_URL,
      KafkaTopic.LOGS_INPUT,
      KafkaTopic.LOGS_GROUP_BY_URL_OUTPUT);
  }

  @Override
  protected String groupBy(LogRequest logRequest) {
    return logRequest.getUrl();
  }
}
