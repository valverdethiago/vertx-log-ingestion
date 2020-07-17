package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogAggregatorByMinuteVerticle extends AbstractLogAggregatorVerticle {


  public LogAggregatorByMinuteVerticle() {
    super(MetricType.GROUP_BY_MINUTE, KafkaTopic.LOGS_INPUT, KafkaTopic.LOGS_GROUP_BY_MINUTE);
  }

  @Override
  protected String groupBy(LogRequest logRequest) {
    return ""+logRequest.getDate().getEpochSecond();
  }
}
