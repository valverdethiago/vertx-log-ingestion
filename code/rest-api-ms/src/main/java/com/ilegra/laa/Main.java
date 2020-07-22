package com.ilegra.laa;

import com.ilegra.laa.models.*;
import com.ilegra.laa.serialization.LogAggregatorSerde;
import com.ilegra.laa.serialization.LogEntrySerde;
import com.ilegra.laa.serialization.LogEntryTimestampExtractor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.KTableAggregate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Main {
  private final static DateFormat DATE_FORMAT = new SimpleDateFormat(DatePattern.DAY.getPattern());


  public static final String APPLICATION_ID = UUID.randomUUID().toString();

  public String bootstrapServers;
  public Topology topology;
  public KafkaStreams streams;
  public Properties streamsConfig;

  public static void main(String[] args) throws Exception {
    String bootstrapServers = "localhost:9092";
    Main app = new Main(bootstrapServers);
    app.build();
    app.run();
  }

  public Main(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  protected void build() {
    streamsConfig = buildStreamsConfig(bootstrapServers);
    StreamsBuilder streamsBuilder = configureStreamsBuilder(new StreamsBuilder());

    this.topology = streamsBuilder.build();
    this.streams = new KafkaStreams(topology, streamsConfig);
  }

  protected void run() throws InterruptedException {
    streams.start();
    final CountDownLatch latch = new CountDownLatch(1);
    latch.await();
    Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
      @Override
      public void run() {
        streams.close();
        latch.countDown();
      }
    });
  }

  protected Properties buildStreamsConfig(String bootstrapServers) {
    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, LogEntrySerde.class);
    properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, LogEntryTimestampExtractor.class);
    properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    return properties;
  }

  protected StreamsBuilder configureStreamsBuilder(StreamsBuilder streamsBuilder) {

    KStream<String, LogEntry> inputStream = streamsBuilder.stream(KafkaTopic.LOGS_INPUT.name(),
      Consumed.with(Serdes.String(), new LogEntrySerde()));
    inputStream.map((key, log) -> {
      Date date = Date.from(log.getDate());
      return new KeyValue<>(DATE_FORMAT.format(date), log);
    })
      .groupByKey()
      .aggregate(LogAggregator::new,
        (key, log, logAgg) -> {
          return logAgg.add(log);
        },
        Materialized.with(Serdes.String(), new LogAggregatorSerde()))
      .toStream()
      .map((key, logAgg) -> {
        logAgg.generateRanking();
        return new KeyValue<>(key, logAgg);
      })
      .to(KafkaTopic.LOGS_GROUP_BY_DAY_OUTPUT.name(), Produced.with(Serdes.String(), new LogAggregatorSerde()));

    return streamsBuilder;
  }

}
