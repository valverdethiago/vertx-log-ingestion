package com.ilegra.laa.verticles;

import com.ilegra.laa.models.EventBusAddress;
import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogRequest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class LogProducerVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(LogProducerVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) {
    final Map<String, String> config = createKafkaProducerConfig();
    final KafkaProducer<String, String> kafkaProducer = KafkaProducer.create(vertx, config);

    vertx.eventBus().localConsumer(EventBusAddress.LOG_RECEIVED.name(), message -> {
      final String key = UUID.randomUUID().toString();
      LogRequest log = (LogRequest) message.body();
      String json = Json.encodePrettily(log);
      LOG.debug("Log received successfully {} | {}", log, json);

      final KafkaProducerRecord<String, String> kafkaProducerRecord = KafkaProducerRecord
        .create(KafkaTopic.LOGS_INPUT.name(), key, json);

      kafkaProducer.send(kafkaProducerRecord, result -> {
        if (result.failed()) {
          LOG.error("message produce error {} : {}", result.cause(), kafkaProducerRecord);
          return;
        }

        LOG.info("message produced. key: {} | value: {}",
          kafkaProducerRecord.key(),
          kafkaProducerRecord.value());
      });
    });

    startPromise.complete();
  }

  private Map<String, String> createKafkaProducerConfig() {
    final Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:9092");
    config.put("key.serializer", StringSerializer.class.getTypeName());
    config.put("value.serializer", StringSerializer.class.getTypeName());
    return config;
  }

}
