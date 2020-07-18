package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.DatePattern;
import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricGroupType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogAggregatorByYearVerticle extends AbstractLogAggregatorVerticle {

  private final static DateFormat DATE_FORMAT = new SimpleDateFormat(DatePattern.YEAR.getPattern());

  public LogAggregatorByYearVerticle() {
    super(MetricGroupType.GROUP_BY_YEAR, KafkaTopic.LOGS_INPUT, KafkaTopic.LOGS_GROUP_BY_YEAR_OUTPUT);
  }

  @Override
  protected String groupBy(LogRequest logRequest) {
    Date date = Date.from(logRequest.getDate());
    return DATE_FORMAT.format(date);
  }
}
