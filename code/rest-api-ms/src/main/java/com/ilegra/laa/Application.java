package com.ilegra.laa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ilegra.laa.injection.ServiceModule;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.vertx.codecs.GroupedRankingEntryCodec;
import com.ilegra.laa.vertx.codecs.LogEntryCodec;
import com.ilegra.laa.vertx.codecs.RankingEntryCodec;
import com.ilegra.laa.vertx.verticles.*;
import com.intapp.vertx.guice.GuiceVerticleFactory;
import com.intapp.vertx.guice.GuiceVertxDeploymentManager;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    try {
      ClusterManager mgr = new HazelcastClusterManager();
      VertxOptions options = new VertxOptions()
        .setClusterManager(mgr)
        .setMaxEventLoopExecuteTime(Long.MAX_VALUE);
      Injector injector = Guice.createInjector(new ServiceModule());
      Vertx.clusteredVertx(options, res -> {
        if(res.succeeded()) {

          Vertx vertx = res.result();

          GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(injector);
          vertx.registerVerticleFactory(guiceVerticleFactory);

          GuiceVertxDeploymentManager deploymentManager = new GuiceVertxDeploymentManager(vertx);
          DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true);

          vertx.eventBus().registerDefaultCodec(LogEntry.class, new LogEntryCodec());
          vertx.eventBus().registerDefaultCodec(RankingEntry.class, new RankingEntryCodec());
          vertx.eventBus().registerDefaultCodec(GroupedRankingEntry.class, new GroupedRankingEntryCodec());
          deploymentManager.deployVerticle(HttpServerVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogProducerVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(MetricUpdateEventListenerVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(GroupedMetricUpdateEventListenerVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByUrlVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByMinuteVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByDayVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByRegionVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByWeekVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByMonthVerticle.class, deploymentOptions);
          deploymentManager.deployVerticle(LogAggregatorByYearVerticle.class, deploymentOptions);

        }
        else {
          log.error("Could not start verticle: ", res.failed());
        }
      });
    }
    catch (Exception e) {
      log.error("Unhandled exception: ", e);
      System.out.println(e.getMessage());
    }
  }
}
