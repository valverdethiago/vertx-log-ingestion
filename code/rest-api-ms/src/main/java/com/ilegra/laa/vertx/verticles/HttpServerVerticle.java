package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.injection.GuiceInjectionProvider;
import com.ilegra.laa.service.HealthCheckService;
import com.ilegra.laa.vertx.controllers.LogIngestionRestController;
import com.ilegra.laa.vertx.controllers.MetricsRestController;
import com.ilegra.laa.vertx.controllers.SimpleMetricsRestController;
import com.zandero.rest.RestBuilder;
import com.ilegra.laa.config.ServerSettings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class HttpServerVerticle extends AbstractVerticle {

  public static final String API_PATH = "/laa";

  private final static Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

  private final ServerSettings settings;
  private final HealthCheckService healthCheckService;

  @Inject
  public HttpServerVerticle(ServerSettings settings, HealthCheckService healthCheckService) {
    this.settings = settings;
    this.healthCheckService = healthCheckService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    new RestBuilder(router)
      .injectWith(new GuiceInjectionProvider(vertx, settings))
      .register(
        LogIngestionRestController.class,
        MetricsRestController.class,
        SimpleMetricsRestController.class)
      .build();
    router.get(API_PATH+"/health").handler(healthCheckService.createHealthCheckHandler());


    vertx.createHttpServer().requestHandler(router).listen(settings.getPort(), res -> {
      if (res.succeeded()) {
        LOG.info("Server is now listening! ".concat("http://localhost:")
          .concat(String.valueOf(res.result().actualPort())
            .concat(API_PATH)));
        startPromise.complete();
      } else {
        LOG.error("Failed to bind!");
        startPromise.fail("Failed to bind");
      }
    });
  }


}
