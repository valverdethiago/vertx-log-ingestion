package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogAggregator;

public class LogAggregatorSerde extends JsonPojoSerde<LogAggregator>{
  public LogAggregatorSerde() {
    super(LogAggregator.class);
  }
}
