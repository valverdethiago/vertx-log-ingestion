package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogAggregator;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.serialization.LogAggregatorDeserializer;
import com.ilegra.laa.serialization.LogAggregatorSerde;
import com.ilegra.laa.serialization.LogEntrySerde;
import io.vertx.core.json.Json;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLogAggregatorVerticle extends AbstractLogStreamVerticle<LogAggregator> {

  private final static Logger LOG = LoggerFactory.getLogger(AbstractLogAggregatorVerticle.class);

  public AbstractLogAggregatorVerticle(MetricGroupType metricGroupType,
                                       KafkaTopic inputTopicName,
                                       KafkaTopic outputTopicName) {
    super(metricGroupType, inputTopicName, outputTopicName, LogAggregatorDeserializer.class);
  }

  /**
   * Define the processing topology for Count.
   *
   * @param builder StreamsBuilder to use
   */
  protected void createAggregatorKafkaStreams(final StreamsBuilder builder) {
    // Construct a `KStream` from the input topic , where message values
    // represent logs sent through API , we ignore whatever may be stored
    // in the message keys).  The default key and value serdes will be used.
    builder.stream(this.inputTopicName.name(),
      Consumed.with(Serdes.String(), new LogEntrySerde()))
      .map( (key, log) -> {
        String groupBy = this.groupBy(log);
        return KeyValue.pair(groupBy, log);
        })
      .groupByKey()
      .aggregate(LogAggregator::new,
        (key, log, logAgg) ->  logAgg.add(log),
        Materialized.with(Serdes.String(), new LogAggregatorSerde()))
      .toStream()
      .map( (key, logAgg) -> {
        logAgg.generateRanking();
        return new KeyValue<>(key, logAgg);
      }).to(this.outputTopicName.name(), Produced.with(Serdes.String(), new LogAggregatorSerde()));
  }

  protected abstract String groupBy(final LogEntry logRequest);

  @Override
  protected String serializeAggregator(LogAggregator aggregator) {
    return Json.encodePrettily(aggregator);
  }
}
