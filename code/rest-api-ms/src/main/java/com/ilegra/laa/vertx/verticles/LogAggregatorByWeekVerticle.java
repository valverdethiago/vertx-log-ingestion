package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LogAggregatorByWeekVerticle extends AbstractLogAggregatorVerticle {

  public LogAggregatorByWeekVerticle() {
    super(MetricGroupType.GROUP_BY_WEEK, KafkaTopic.LOGS_INPUT, KafkaTopic.LOGS_GROUP_BY_WEEK_OUTPUT);
  }

  @Override
  protected String groupBy(LogEntry logRequest) {
    ZonedDateTime zdt = ZonedDateTime.ofInstant(logRequest.getDate(), ZoneId.systemDefault());
    Calendar calendar = GregorianCalendar.from(zdt);
    return calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.WEEK_OF_YEAR);
  }
}
