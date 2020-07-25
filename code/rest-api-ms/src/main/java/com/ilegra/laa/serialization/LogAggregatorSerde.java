package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogAggregator;

/**
 * Serde for processing @{@link LogAggregator} json messages to and from kafka topics
 *
 * @author valverde.thiago
 */
public class LogAggregatorSerde extends JsonPojoSerde<LogAggregator>{
  public LogAggregatorSerde() {
    super(LogAggregator.class);
  }
}
