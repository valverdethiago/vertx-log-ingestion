package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.*;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.serialization.LogAggregatorSerde;
import com.ilegra.laa.serialization.LogEntrySerde;
import com.ilegra.laa.serialization.RankingEntryDeserializer;
import com.ilegra.laa.serialization.RankingEntrySerde;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogAggregatorByMinuteVerticle extends AbstractLogStreamVerticle<RankingEntry> {

  private final static DateFormat DATE_FORMAT = new SimpleDateFormat(DatePattern.MINUTE.getPattern());

  public LogAggregatorByMinuteVerticle() {
    super(MetricGroupType.GROUP_BY_MINUTE,
      KafkaTopic.LOGS_INPUT,
      KafkaTopic.LOGS_GROUP_BY_MINUTE_OUTPUT,
      RankingEntryDeserializer.class);
  }


  @Override
  protected void createAggregatorKafkaStreams(StreamsBuilder builder) {
    builder.stream(this.inputTopicName.name(),
      Consumed.with(Serdes.String(), new LogEntrySerde()))
      .map( (key, log) -> KeyValue.pair( this.groupBy(log), log ))
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

  private String groupBy(LogEntry logEntry) {
    Date date = Date.from(logEntry.getDate());
    return DATE_FORMAT.format(date);
  }


}
