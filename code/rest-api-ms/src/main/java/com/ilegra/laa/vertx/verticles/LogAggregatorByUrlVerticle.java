package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAggregatorByUrlVerticle extends AbstractLogAggregatorVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(LogAggregatorByUrlVerticle.class);

  public LogAggregatorByUrlVerticle() {
    super(MetricType.GROUP_BY_URL, KafkaTopic.LOGS_INPUT, KafkaTopic.LOGS_GROUP_BY_URL_OUTPUT);
  }

  @Override
  protected String groupBy(LogRequest logRequest) {
    return logRequest.getUrl();
  }
}
