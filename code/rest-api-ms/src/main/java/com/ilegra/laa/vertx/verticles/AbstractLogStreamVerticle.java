package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.config.ServerSettings;
import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.serialization.LogEntrySerde;
import com.ilegra.laa.service.HealthCheckService;
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

/**
 * Abstract verticle to reduce boilerplate code in aggregation process with Kafka Streams
 * @param <T> Type to be serialized at the end of aggregation process
 *
 * @author valverde.thiago
 */
public abstract class AbstractLogStreamVerticle<T extends Serializable> extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(AbstractLogStreamVerticle.class);

  protected final ServerSettings settings;
  /**
   * Id for the grouping process
   */
  protected final MetricGroupType metricGroupType;
  /**
   * Topic to be consumed and start the aggregation process
   */
  protected final KafkaTopic inputTopicName;
  /**
   * Topic to be ingested with final aggregation information
   */
  protected final KafkaTopic outputTopicName;
  /**
   * Default deserializer class for the type being sent to output topic
   */
  protected final Class<? extends Deserializer<T>> deserializerClass;

  /**
   * Kafka Streams to be started within the verticle
   */
  private KafkaStreams streams;
  /**
   * Kafka Consumer to be listening to the output topic to deliver the final result of aggregation process
   */
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

  /**
   * Starts streaming process from the input kafka topic
   *
   * @param startPromise global vertical promise
   */
  public void startStreams(Promise<Void> startPromise) {
    //starting streams can take a while, therefore we do it asynchronously
    vertx.<KafkaStreams>executeBlocking(future -> {
      // Configure the Streams application
      final Properties streamsConfiguration = getStreamsConfiguration();
      final StreamsBuilder builder = new StreamsBuilder();
      LOG.info("Starting Kafka Streams {}", this.getClass().getSimpleName());
      createAggregatorKafkaStreams(builder);
      this.streams = new KafkaStreams(builder.build(), streamsConfiguration);
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

  /**
   * Starts the consumer of output topic to deliver the metrics update to the final target
   *
   * @param startPromise global verticle promise
   */
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

  /**
   * Handler method that deliveries metrics updates to the event bus
   *
   * @param record
   */
  private void handleMetricUpdates(KafkaConsumerRecord<String, T> record) {
    LOG.debug("Processing topic = {}, key= {},value= {}, partition = {}, offset = {}", this.outputTopicName.name(),
      record.key(), record.value(), record.partition(), record.offset());
    vertx.eventBus().send(this.metricGroupType.name(), record.value());
  }


  /**
   * Configure the Streams application.
   * <p>
   * Various Kafka Streams related settings are defined here such as the location of the target Kafka cluster to use.
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

  /**
   * Configure the Consumer application.
   *
   * @return Properties getConsumerConfiguration
   */
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
