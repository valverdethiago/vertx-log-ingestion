package com.ilegra.laa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ilegra.laa.injection.ServiceModule;
import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.service.HealthCheckService;
import com.ilegra.laa.vertx.codecs.GroupedRankingEntryCodec;
import com.ilegra.laa.vertx.codecs.LogEntryCodec;
import com.ilegra.laa.vertx.codecs.RankingEntryCodec;
import com.ilegra.laa.vertx.verticles.*;
import com.intapp.vertx.guice.GuiceVerticleFactory;
import com.intapp.vertx.guice.GuiceVertxDeploymentManager;
import com.zandero.cmd.CommandLineException;
import com.ilegra.laa.config.ServerSettings;
import io.vertx.core.*;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Application {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  ServerSettings settings = new ServerSettings();

  public static void main(String[] args) {
    new Application().run(args);
  }

  public void run(String[] args) {
    try {
      settings.parse(args);
      ClusterManager mgr = new HazelcastClusterManager();
      VertxOptions options = new VertxOptions()
        .setClusterManager(mgr)
        .setMaxEventLoopExecuteTime(Long.MAX_VALUE);
      Vertx.clusteredVertx(options, res -> {
        if (res.succeeded()) {

          Vertx vertx = res.result();
          Injector injector = Guice.createInjector(new ServiceModule(vertx, settings));

          GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(injector);
          vertx.registerVerticleFactory(guiceVerticleFactory);

          GuiceVertxDeploymentManager deploymentManager = new GuiceVertxDeploymentManager(vertx);
          DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true);

          vertx.eventBus().registerDefaultCodec(LogEntry.class, new LogEntryCodec());
          vertx.eventBus().registerDefaultCodec(RankingEntry.class, new RankingEntryCodec());
          vertx.eventBus().registerDefaultCodec(GroupedRankingEntry.class, new GroupedRankingEntryCodec());
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
          deploymentManager.deployVerticle(HttpServerVerticle.class, deploymentOptions);
        } else {
          log.error("Could not start verticle: ", res.failed());
        }
      });
    } catch (CommandLineException ex) {
      log.error("Invalid settings: ", ex);
      showHelp(settings.getHelp());
    } catch (Exception e) {
      log.error("Unhandled exception: ", e);
      System.out.println(e.getMessage());
    }
  }

  private <T extends AbstractVerticle> Future<Object> futureDeployment(
    GuiceVertxDeploymentManager deploymentManager,
    DeploymentOptions deploymentOptions,
    Class<T> verticleClass) {
    return Future.future(promise -> {
      deploymentManager.deployVerticle(verticleClass, deploymentOptions, handler -> {
        if (handler.succeeded())
          promise.complete();
        else
          promise.fail("Could not deploy verticle " + verticleClass);
      });
    });
  }

  private void showHelp(List<String> help) {
    help.forEach(System.out::println);
  }
}
