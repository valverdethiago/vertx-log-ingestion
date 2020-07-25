package com.ilegra.laa.config;

import com.zandero.cmd.CommandBuilder;
import com.zandero.cmd.CommandLineException;
import com.zandero.cmd.CommandLineParser;
import com.zandero.cmd.option.CommandOption;
import com.zandero.cmd.option.IntOption;
import com.zandero.cmd.option.StringOption;
import com.zandero.settings.Settings;

import java.util.List;

/**
 * Wrapper to store and load all configs
 * @author valverde.thiago
 */
public class ServerSettings extends Settings {

  private static final String PORT = "port";
  private static final String KAFKA_SERVER = "kafka";
  private static final String REDIS_HOST = "redisHost";
  private static final String REDIS_PORT = "redisPort";
  private static final String REDIS_PASSWORD = "redisPassword";

  private final CommandBuilder builder;


  public ServerSettings() {
    CommandOption port = new IntOption("p")
      .longCommand(PORT)
      .setting(PORT)
      .description("Listening on port")
      .defaultsTo(8080);
    CommandOption kafka = new StringOption("k")
      .longCommand(KAFKA_SERVER)
      .setting(KAFKA_SERVER)
      .description("Kafka broker connection")
      .defaultsTo("localhost:9092");
    CommandOption redisHost = new StringOption("rh")
      .longCommand(REDIS_HOST)
      .setting(REDIS_HOST)
      .description("Redis host")
      .defaultsTo("localhost");
    CommandOption redisPort = new IntOption("rp")
      .longCommand(REDIS_PORT)
      .setting(REDIS_PORT)
      .description("Redis Port")
      .defaultsTo(6379);
    CommandOption redisPassword = new StringOption("rpwd")
      .longCommand(REDIS_PASSWORD)
      .setting(REDIS_PASSWORD)
      .description("Redis password")
      .defaultsTo("Illegra2020");
    builder = new CommandBuilder();
    builder.add(port);
    builder.add(kafka);
    builder.add(redisHost);
    builder.add(redisPort);
    builder.add(redisPassword);
  }

  public void parse(String args[]) throws CommandLineException {
    CommandLineParser parser = new CommandLineParser(builder);
    this.putAll((parser.parse(args)));
  }

  public List<String> getHelp() {
    return builder.getHelp();
  }

  public Integer getPort() {
    return getInt(PORT);
  }

  public String getKafkaServer() {
    return getString(KAFKA_SERVER);
  }

  public String getRedisHost() {
    return getString(REDIS_HOST);
  }

  public Integer getRedisPort() {
    return getInt(REDIS_PORT);
  }

  public String getRedisPassword() {
    return getString(REDIS_PASSWORD);
  }
}
