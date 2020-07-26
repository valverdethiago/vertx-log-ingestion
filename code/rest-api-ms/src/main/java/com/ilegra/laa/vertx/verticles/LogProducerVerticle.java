package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.models.EventBusAddress;
import com.ilegra.laa.models.KafkaTopic;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.serialization.LogEntrySerializer;
import com.ilegra.laa.config.ServerSettings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Verticle that consumes log entries from event bus and send it to kafka input log topic
 *
 * @author valverde.thiago
 */
public class LogProducerVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(LogProducerVerticle.class);
  private KafkaProducer<String, LogEntry> kafkaProducer;
  private final ServerSettings settings;

  @Inject
  public LogProducerVerticle(ServerSettings settings) {
    this.settings = settings;
  }

  @Override
  public void start(final Promise<Void> startPromise) {
    final Map<String, String> config = createKafkaProducerConfig();
    kafkaProducer = KafkaProducer.create(vertx, config);
    vertx.eventBus().localConsumer(EventBusAddress.LOG_RECEIVED.name(), this::consumeMessage);
    startPromise.complete();
  }

  private void consumeMessage(Message<LogEntry> message) {
    String json = Json.encodePrettily(message.body());
    LOG.debug("Log received successfully {} | {}", message.body(), json);

    final KafkaProducerRecord<String, LogEntry> kafkaProducerRecord = KafkaProducerRecord
      .create(KafkaTopic.LOGS_INPUT.name(), message.body().getId().toString(), message.body());

    kafkaProducer.send(kafkaProducerRecord, result -> {
      if (result.failed()) {
        LOG.error("message produce error {} : {}", result.cause(), kafkaProducerRecord);
        return;
      }

      LOG.info("message produced. key: {} | value: {}",
        kafkaProducerRecord.key(),
        kafkaProducerRecord.value());
    });
  }

  private Map<String, String> createKafkaProducerConfig() {
    final Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", settings.getKafkaServer());
    config.put("key.serializer", StringSerializer.class.getTypeName());
    config.put("value.serializer", LogEntrySerializer.class.getTypeName());
    return config;
  }

}
