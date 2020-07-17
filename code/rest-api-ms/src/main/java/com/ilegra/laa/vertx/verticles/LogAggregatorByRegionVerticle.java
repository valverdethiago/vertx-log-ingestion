package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAggregatorByRegionVerticle extends AbstractLogAggregatorVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(LogAggregatorByRegionVerticle.class);

  public LogAggregatorByRegionVerticle() {
    super(MetricType.GROUP_BY_REGION, KafkaTopic.LOGS_INPUT, KafkaTopic.LOGS_GROUP_BY_REGION_OUTPUT);
  }

  @Override
  protected String groupBy(LogRequest logRequest) {
    return logRequest.getRegion().getName();
  }
}
