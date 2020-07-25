package com.ilegra.laa.models;

/**
 * Store all topic names for input and output messages on kafka
 *
 * @author valverde.thiago
 */
public enum KafkaTopic {

  LOGS_INPUT,
  LOGS_GROUP_BY_URL_OUTPUT,
  LOGS_GROUP_BY_REGION_OUTPUT,
  LOGS_GROUP_BY_DAY_OUTPUT,
  LOGS_GROUP_BY_WEEK_OUTPUT,
  LOGS_GROUP_BY_MONTH_OUTPUT,
  LOGS_GROUP_BY_YEAR_OUTPUT,
  LOGS_GROUP_BY_MINUTE_OUTPUT;
}
