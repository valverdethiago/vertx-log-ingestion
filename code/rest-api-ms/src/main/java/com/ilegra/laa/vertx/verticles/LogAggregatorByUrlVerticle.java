package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.serialization.LogEntrySerde;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class LogAggregatorByUrlVerticle extends AbstractLogStreamVerticle<Long> {

  public LogAggregatorByUrlVerticle() {
    super(MetricGroupType.GROUP_BY_URL,
      KafkaTopic.LOGS_INPUT,
      KafkaTopic.LOGS_GROUP_BY_URL_OUTPUT,
      LongDeserializer.class);
  }

  @Override
  protected void createAggregatorKafkaStreams(StreamsBuilder builder) {
    builder.stream(this.inputTopicName.name(),
      Consumed.with(Serdes.String(), new LogEntrySerde()))
      .groupBy( (key, log) -> log.getUrl() )
      .count(Materialized.with(Serdes.String(), Serdes.Long()))
      .toStream()
      .to(this.outputTopicName.name(), Produced.with(Serdes.String(), Serdes.Long()));
  }

  @Override
  protected String serializeAggregator(Long aggregator) {
    return aggregator.toString();
  }
}
