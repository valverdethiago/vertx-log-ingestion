package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.serialization.LogEntrySerde;
import com.ilegra.laa.config.ServerSettings;
import com.ilegra.laa.vertx.health.KafkaStateListener;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;

public abstract class AbstractLogStreamVerticle<T extends Serializable> extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(AbstractLogStreamVerticle.class);

  protected final ServerSettings settings;
  protected final MetricGroupType metricGroupType;
  protected final KafkaTopic inputTopicName;
  protected final KafkaTopic outputTopicName;
  protected final Class<? extends Deserializer<T>> deserializerClass;

  private KafkaStreams streams;
  private KafkaConsumer<String, T> consumer;

  public AbstractLogStreamVerticle(ServerSettings settings,
                                   MetricGroupType metricGroupType,
                                   KafkaTopic inputTopicName,
                                   KafkaTopic outputTopicName,
                                   Class<? extends Deserializer<T>> deserializerClass) {
    this.settings = settings;
    this.metricGroupType = metricGroupType;
    this.inputTopicName = inputTopicName;
    this.outputTopicName = outputTopicName;
    this.deserializerClass = deserializerClass;
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
      this.streams.setStateListener(
        new KafkaStateListener(this.metricGroupType.name(), vertx.sharedData())
      );
      streams.cleanUp();
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

  private void handleMetricUpdates(KafkaConsumerRecord<String, T> record) {
    /*
    SharedData sd = vertx.sharedData();
    LocalMap<String, String> map1 = sd.getLocalMap(this.metricGroupType.name());
    map1.put(record.key(), this.serializeAggregator(record.value()));
    */
    LOG.debug("Processing topic = {}, key= {},value= {}, partition = {}, offset = {}", this.outputTopicName.name(),
      record.key(), record.value(), record.partition(), record.offset());
    vertx.eventBus().send(this.metricGroupType.name(), record.value());
  }


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
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "log-analytics-application-"+this.metricGroupType.name());
    config.put(StreamsConfig.CLIENT_ID_CONFIG, "log-analytics-application-client-"+this.metricGroupType.name());
    // Where to find Kafka broker(s).
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, settings.getKafkaServer());
    // Specify default (de)serializers for record keys and for record values.
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getTypeName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, LogEntrySerde.class.getTypeName());
    config.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/laa/state-dir");
    // Records should be flushed every 10 seconds. This is less than the default
    // in order to keep this example interactive.
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1 * 1000);
    // For illustrative purposes we disable record caches.
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    return config;
  }

  private Properties getConsumerConfiguration() {
    final Properties config = new Properties();
    config.put("bootstrap.servers", settings.getKafkaServer());
    config.put("key.deserializer", StringDeserializer.class.getTypeName());
    config.put("value.deserializer", this.deserializerClass.getTypeName());
    config.put("group.id", "log-access-analytics-consumer-"+this.metricGroupType.name());
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");
    return config;
  }


  /**
   * Define the processing topology for aggregation.
   *
   * @param builder StreamsBuilder to use
   */
  protected abstract void createAggregatorKafkaStreams(final StreamsBuilder builder);

}
