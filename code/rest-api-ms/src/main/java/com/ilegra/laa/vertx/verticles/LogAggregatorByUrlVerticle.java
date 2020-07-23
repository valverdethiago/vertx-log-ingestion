package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogAggregator;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.serialization.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class LogAggregatorByUrlVerticle extends AbstractLogStreamVerticle<RankingEntry> {

  public LogAggregatorByUrlVerticle() {
    super(MetricGroupType.GROUP_BY_URL,
      KafkaTopic.LOGS_INPUT,
      KafkaTopic.LOGS_GROUP_BY_URL_OUTPUT,
      RankingEntryDeserializer.class);
  }

  @Override
  protected void createAggregatorKafkaStreams(StreamsBuilder builder) {
    builder.stream(this.inputTopicName.name(),
      Consumed.with(Serdes.String(), new LogEntrySerde()))
      .map( (key, log) -> KeyValue.pair(log.getUrl(), log))
      .groupByKey()
      .aggregate(LogAggregator::new,
        (key, log, logAgg) -> {
          logAgg.getUrls().add(log);
          return logAgg;
        },
        Materialized.with(Serdes.String(), new LogAggregatorSerde()))
      .toStream()
      .map( (key, logAgg) ->  {
        return new KeyValue<>(key, new RankingEntry(key, logAgg.countUrls()));
      })
      .to(this.outputTopicName.name(), Produced.with(Serdes.String(), new RankingEntrySerde()));
  }
}
