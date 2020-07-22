package com.ilegra.laa.vertx.controllers;

import com.ilegra.laa.models.MetricGroupType;
import io.vertx.core.Vertx;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/laa/metrics-status")
public class SimpleMetricsRestController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response handleMetrics(@Context Vertx vertx) {
    Map<?, ?> map = aggregateMetrics(vertx);
    if (map.isEmpty())
      return Response.noContent().build();
    else
      return Response.ok(map).build();
  }
  @GET
  @Path("/:param")
  public String execute(@PathParam("param") String parameter) {
    return parameter;
  }

  private Map<String, Map<String, String>> aggregateMetrics(Vertx vertx) {
    Map<String, Map<String, String>> map = new HashMap<>();
    map.put(MetricGroupType.GROUP_BY_URL.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_URL.name()));
    map.put(MetricGroupType.GROUP_BY_REGION.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_REGION.name()));
    map.put(MetricGroupType.GROUP_BY_DAY.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_DAY.name()));
    map.put(MetricGroupType.GROUP_BY_WEEK.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_WEEK.name()));
    map.put(MetricGroupType.GROUP_BY_MONTH.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MONTH.name()));
    map.put(MetricGroupType.GROUP_BY_YEAR.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_YEAR.name()));
    map.put(MetricGroupType.GROUP_BY_MINUTE.name(),
      vertx.sharedData().getLocalMap(MetricGroupType.GROUP_BY_MINUTE.name()));
    return map;
  }
}
