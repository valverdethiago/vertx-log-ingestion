package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.service.MetricCacheService;
import com.ilegra.laa.vertx.health.ComponentState;
import com.ilegra.laa.vertx.health.HealthResponseWrapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.LocalMap;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ilegra.laa.vertx.health.ComponentState.*;
import static com.ilegra.laa.vertx.verticles.HttpServerVerticle.API_PATH;

@Path(API_PATH+"/health")
public class HealthCheckRestController {
  @Inject
  private MetricCacheService metricCacheService;
  @Inject
  private Vertx vertx;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response healthCheck()  {
    HealthResponseWrapper responseWrapper = this.getComponentStateMap();
    List<ComponentState> nonWorkingStreams =
      responseWrapper.getKafkaStreamStateMap().entrySet().stream()
        .filter(entry -> !entry.getValue().isWorking())
        .map(entry-> entry.getValue())
        .collect(Collectors.toList());
    boolean everyThingIsWorking = nonWorkingStreams.isEmpty() && responseWrapper.getRedisConnectionState().isWorking();

    return Response
      .status(everyThingIsWorking ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
      .entity(responseWrapper)
      .build();
  }

  private HealthResponseWrapper getComponentStateMap() {
    LocalMap<String, String> componentStateMap
      = vertx.sharedData().getLocalMap(COMPONENTS_STATE_MAP);
    Map<String, ComponentState> result = componentStateMap.entrySet()
      .stream()
      .map(entry -> new AbstractMap.SimpleEntry<>(
        entry.getKey(),
        Json.decodeValue(entry.getValue(), ComponentState.class)))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    return HealthResponseWrapper.builder()
      .kafkaStreamStateMap(result)
      .redisConnectionState(metricCacheService.getRedisConnectionState())
      .build();
  }

}
