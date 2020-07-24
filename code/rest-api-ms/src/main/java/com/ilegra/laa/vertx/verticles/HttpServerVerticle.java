package com.ilegra.laa.vertx.verticles;

import com.ilegra.laa.injection.GuiceInjectionProvider;
import com.ilegra.laa.vertx.controllers.LogIngestionRestController;
import com.ilegra.laa.vertx.controllers.MetricsRestController;
import com.ilegra.laa.vertx.controllers.SimpleMetricsRestController;
import com.zandero.rest.RestBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HttpServerVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

  public static Optional<String> portCloud = Optional.ofNullable(System.getenv("PORT"));

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    new RestBuilder(router)
      .injectWith(new GuiceInjectionProvider())
      .register(LogIngestionRestController.class,
        MetricsRestController.class,
        SimpleMetricsRestController.class)
      .build();


    vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(portCloud.orElse("8080")), res -> {
      if (res.succeeded()) {
        LOG.info("Server is now listening! ".concat("http://localhost:")
          .concat(String.valueOf(res.result().actualPort())
            .concat("/laa")));
        startPromise.complete();
      } else {
        LOG.error("Failed to bind!");
        startPromise.fail("Failed to bind");
      }
    });
  }


}
