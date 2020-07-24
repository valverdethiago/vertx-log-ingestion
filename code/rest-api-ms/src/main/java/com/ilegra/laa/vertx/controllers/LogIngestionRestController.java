package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.AwsRegion;
import com.ilegra.laa.models.EventBusAddress;
import com.ilegra.laa.models.LogEntry;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ilegra.laa.vertx.verticles.HttpServerVerticle.API_PATH;

@Path(API_PATH+"/ingest")
public class LogIngestionRestController {

  private final static Logger LOG = LoggerFactory.getLogger(LogIngestionRestController.class);

  private static String VALID_URL_REGEX = "(/.+)+\\s(\\d+)\\s([\\w\\-?]+)\\s([1-3])";
  private static String REPLACE_IDS_IN_URL_REGEX = "(?<=/)\\d+(?=/?)";
  private static String ID_REPLACEMENT = "{id}";

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response handleLogIngestion(String ingestedLog, @Context Vertx vertx) {
    Optional<LogEntry> logRequest = this.parseLog(ingestedLog);
    if(logRequest.isPresent()) {
      LogEntry log = logRequest.get();
      LOG.debug("Log parsed successfully: {}", log);
      vertx.eventBus().send(EventBusAddress.LOG_RECEIVED.name(), log);
      return Response.accepted(log).build();
    } else {
      throw new BadRequestException("Invalid Log format");
    }
  }

  private Optional<LogEntry> parseLog(String ingestedLog) {
    Pattern pattern = Pattern.compile(VALID_URL_REGEX);
    Matcher matcher = pattern.matcher(ingestedLog);
    if (matcher.find()) {
      Optional<AwsRegion> region = AwsRegion.from(Integer.valueOf(matcher.group(4)));
      if (region.isPresent()) {
        return Optional.of(
          LogEntry
            .builder()
            .id(UUID.randomUUID())
            .url(matcher.group(1).replaceAll(REPLACE_IDS_IN_URL_REGEX, ID_REPLACEMENT))
            .date(Instant.ofEpochSecond(Long.parseLong(matcher.group(2))))
            .clientId(matcher.group(3))
            .region(region.get())
            .build()
        );
      }
    }
    return Optional.empty();
  }
}
