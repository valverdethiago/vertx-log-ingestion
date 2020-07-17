package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogAggregatorByDayVerticle extends AbstractLogAggregatorVerticle {

  private final static String DATE_PATTERN = "yyyy-MM-dd";
  private final static DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

  public LogAggregatorByDayVerticle() {
    super(MetricType.GROUP_BY_DAY, KafkaTopic.LOGS_INPUT, KafkaTopic.LOGS_GROUP_BY_DAY_OUTPUT);
  }

  @Override
  protected String groupBy(LogRequest logRequest) {
    Date date = Date.from(logRequest.getDate());
    return DATE_FORMAT.format(date);
  }
}
