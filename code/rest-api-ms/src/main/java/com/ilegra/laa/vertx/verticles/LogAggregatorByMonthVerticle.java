package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.config.ServerSettings;
import com.ilegra.laa.models.DatePattern;
import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.service.HealthCheckService;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Verticle that produces aggregation by month and url
 *
 * @author valverde.thiago
 */
public class LogAggregatorByMonthVerticle extends AbstractLogAggregatorVerticle {

  private final static DateFormat DATE_FORMAT = new SimpleDateFormat(DatePattern.MONTH.getPattern());

  @Inject
  public LogAggregatorByMonthVerticle(ServerSettings settings) {
    super(
      settings,
      MetricGroupType.GROUP_BY_MONTH,
      KafkaTopic.LOGS_INPUT,
      KafkaTopic.LOGS_GROUP_BY_MONTH_OUTPUT);
  }

  @Override
  protected String groupBy(LogEntry logEntry) {
    Date date = Date.from(logEntry.getDate());
    return DATE_FORMAT.format(date);
  }
}
