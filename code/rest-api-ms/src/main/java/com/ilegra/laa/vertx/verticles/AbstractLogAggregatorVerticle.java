package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import com.ilegra.laa.models.MetricType;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

public abstract class AbstractLogAggregatorVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(AbstractLogAggregatorVerticle.class);

  private final MetricType metricType;
  private final KafkaTopic inputTopicName;
  private final KafkaTopic outputTopicName;

  private KafkaStreams streams;
  private KafkaConsumer<String, Long> consumer;

  public AbstractLogAggregatorVerticle(MetricType metricType, KafkaTopic inputTopicName, KafkaTopic outputTopicName) {
    this.metricType = metricType;
    this.inputTopicName = inputTopicName;
    this.outputTopicName = outputTopicName;
  }


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.startStreams(startPromise);
    this.startConsumer(startPromise);
  }

  public void startStreams(Promise<Void> startPromise) throws Exception {
    //starting streams can take a while, therefore we do it asynchronously
    vertx.<KafkaStreams>executeBlocking(future -> {
      // Configure the Streams application
      final Properties streamsConfiguration = getStreamsConfiguration();
      final StreamsBuilder builder = new StreamsBuilder();
      LOG.info("Starting Kafka Streams {}", this.getClass().getSimpleName());
      createAggregatorKafkaStreams(builder);
      this.streams = new KafkaStreams(builder.build(), streamsConfiguration);
//      streams.cleanUp();
      streams.start();
    }, res ->{
      if (res.succeeded()) {
        LOG.info("Started Kafka Streams {}", this.getClass().getSimpleName());
        startPromise.complete();
      } else {
        LOG.error("Starting Kafka Streams {} failed because of ", this.getClass().getSimpleName(), res.cause());
        startPromise.fail(res.cause());
      }
    });
  }

  private void startConsumer(Promise<Void> startPromise) {
    vertx.<KafkaStreams>executeBlocking(future -> {
      consumer = KafkaConsumer.create(vertx, this.getConsumerConfiguration());
      consumer.handler(this::handleMetricUpdates);
      consumer.subscribe(this.outputTopicName.name());
    }, res ->{
      if (res.succeeded()) {
        LOG.info("Started Kafka Consumer {}", this.getClass().getSimpleName());
        startPromise.complete();
      } else {
        LOG.error("Starting Kafka Consumer {} failed because of {}", this.getClass().getSimpleName(), res.cause());
        startPromise.fail(res.cause());
      }
    });
  }

  @Override
  public void stop() throws Exception {
    this.stopStreams();
    this.stopConsumer();
  }

  private void stopStreams() throws Exception {
    vertx.<Void>executeBlocking(future -> {
      LOG.info("Shutting down Kafka Streams {}", this.getClass().getSimpleName());
      streams.close();
      future.complete();
    }, msg -> {
    });
  }

  private void stopConsumer() throws Exception {
    vertx.<Void>executeBlocking(future -> {
      LOG.info("Shutting down Kafka Consumer {}", this.getClass().getSimpleName());
      consumer.close();
      future.complete();
    }, msg -> {
    });
  }

  private void handleMetricUpdates(KafkaConsumerRecord<String, Long> record) {
    SharedData sd = vertx.sharedData();
    LocalMap<String, Long> map1 = sd.getLocalMap(this.metricType.name());
    map1.put(record.key(), record.value());
    LOG.debug("Processing topic = {}, key= {},value= {}, partition = {}, offset = {}", this.outputTopicName.name(),
      record.key(), record.value(), record.partition(), record.offset());
  }


  /**
   * Define the processing topology for Count.
   *
   * @param builder StreamsBuilder to use
   */
  private void createAggregatorKafkaStreams(final StreamsBuilder builder) {
    // Construct a `KStream` from the input topic , where message values
    // represent logs sent through API , we ignore whatever may be stored
    // in the message keys).  The default key and value serdes will be used.
    final KTable<String, String> jsonLogs = builder.table(this.inputTopicName.name());

    final KTable<String, Long> wordCounts = jsonLogs
      .groupBy((keyIgnored, log) -> {
        LogRequest logObj = Json.decodeValue(log, LogRequest.class);
        String groupBy = this.groupBy(logObj);
        return KeyValue.pair(groupBy, groupBy);
      })
      // Count the occurrences of each group (record key).
      .count();

    // Write the `KTable<String, Long>` to the output topic.
    wordCounts.toStream().to(this.outputTopicName.name(), Produced.with(Serdes.String(), Serdes.Long()));
  }

  protected abstract String groupBy(final LogRequest logRequest);


  /**
   * Configure the Streams application.
   * <p>
   * Various Kafka Streams related settings are defined here such as the location of the target Kafka cluster to use.
   * Additionally, you could also define Kafka Producer and Kafka Consumer settings when needed.
   *
   * @return Properties getStreamsConfiguration
   */
  private Properties getStreamsConfiguration() {
    final Properties config = new Properties();
    // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
    // against which the application is run.
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "log-access-analytics-"+this.metricType.name());
    config.put(StreamsConfig.CLIENT_ID_CONFIG, "log-access-analytics-client-"+this.metricType.name());
    // Where to find Kafka broker(s).
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    // Specify default (de)serializers for record keys and for record values.
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    // Records should be flushed every 10 seconds. This is less than the default
    // in order to keep this example interactive.
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
    // For illustrative purposes we disable record caches.
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    // Use a temporary directory for storing state, which will be automatically removed after the test.
//    config.put(StreamsConfig.STATE_DIR_CONFIG, tempDirectory().getAbsolutePath());
    return config;
  }

  private Properties getConsumerConfiguration() {
    final Properties config = new Properties();
    config.put("bootstrap.servers", "localhost:9092");
    config.put("key.deserializer", StringDeserializer.class.getTypeName());
    config.put("value.deserializer", LongDeserializer.class.getTypeName());
    config.put("group.id", "log-access-analytics-consumer:"+this.metricType.name());
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");
    return config;
  }
/*
  public static File tempDirectory() {
    final File file;
    try {
      file = Files.createTempDirectory("confluent").toFile();
    } catch (IOException var2) {
      throw new RuntimeException("Failed to create a temp dir", var2);
    }

    file.deleteOnExit();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          delete(file);
        } catch (IOException var2) {
          System.out.println("Error deleting " + file.getAbsolutePath());
        }

      }
    });
    return file;
  }

  public static void delete(final File file) throws IOException {
    if (file != null) {
      Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
        public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
          if (exc instanceof NoSuchFileException && path.toFile().equals(file)) {
            return FileVisitResult.TERMINATE;
          } else {
            throw exc;
          }
        }

        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
          Files.delete(path);
          return FileVisitResult.CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path path, IOException exc) throws IOException {
          Files.delete(path);
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }
  */
}
