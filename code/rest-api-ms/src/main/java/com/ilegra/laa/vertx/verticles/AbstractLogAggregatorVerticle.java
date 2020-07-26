package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.config.ServerSettings;
import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogAggregator;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.serialization.GroupedRankingEntryDeserializer;
import com.ilegra.laa.serialization.GroupedRankingEntrySerde;
import com.ilegra.laa.serialization.LogAggregatorSerde;
import com.ilegra.laa.serialization.LogEntrySerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.ArrayList;

/**
 * Abstract verticle to produce more complex aggregations with more than one level, for instance region and url
 *
 * @author valverde.thiago
 */
public abstract class AbstractLogAggregatorVerticle extends AbstractLogStreamVerticle<GroupedRankingEntry> {

  public AbstractLogAggregatorVerticle(ServerSettings settings,
                                       MetricGroupType metricGroupType,
                                       KafkaTopic inputTopicName,
                                       KafkaTopic outputTopicName) {
    super(settings,
      metricGroupType,
      inputTopicName,
      outputTopicName,
      GroupedRankingEntryDeserializer.class);
  }

  /**
   * Define the processing topology for aggregation
   *
   * @param builder StreamsBuilder to use
   */
  protected void createAggregatorKafkaStreams(final StreamsBuilder builder) {
    // Construct a `KStream` from the input topic , where message values
    // represent logs sent through API , we ignore whatever may be stored
    // in the message keys).
    builder.stream(this.inputTopicName.name(),
      Consumed.with(Serdes.String(), new LogEntrySerde()))
      .map( (key, log) -> {
        String groupBy = this.groupBy(log);
        return KeyValue.pair(groupBy, log);
        })
      .groupByKey()
      //first aggregation is done here, accumulating the logs into LogAggregator object
      .aggregate(LogAggregator::new,
        (key, log, logAgg) ->  logAgg.add(log),
        Materialized.with(Serdes.String(), new LogAggregatorSerde()))
      .toStream()
      //after accumulating the logs into intermediate object we transform the output stream
      //to generate the url ranking
      .map( (key, logAgg) -> {
        logAgg.generateRanking();
        GroupedRankingEntry entry = new GroupedRankingEntry(key, new ArrayList<>(logAgg.getRanking()));
        return new KeyValue<>(key, entry);
      })
      .to(this.outputTopicName.name(), Produced.with(Serdes.String(), new GroupedRankingEntrySerde()));
  }

  /**
   * Defines the key for the first aggregation
   *
   * @param logEntry
   * @return
   */
  protected abstract String groupBy(final LogEntry logEntry);
}
