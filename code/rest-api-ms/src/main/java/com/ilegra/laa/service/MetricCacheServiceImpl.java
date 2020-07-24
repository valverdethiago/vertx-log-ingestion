package com.ilegra.laa.service;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.search.MetricResponseWrapper;
import com.ilegra.laa.models.search.SearchFilter;
import com.ilegra.laa.models.search.SearchOrder;
import com.ilegra.laa.models.builders.MetricResponseWrapperBuilder;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import com.ilegra.laa.config.ServerSettings;
import com.ilegra.laa.vertx.health.ComponentState;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetricCacheServiceImpl implements MetricCacheService {

  private final static Logger LOG = LoggerFactory.getLogger(MetricCacheServiceImpl.class);

  private final ServerSettings settings;
  private final Vertx vertx;


  @Inject
  public MetricCacheServiceImpl(Vertx vertx, ServerSettings settings) {
    this.vertx = vertx;
    this.settings = settings;
  }

  private RedisClient getRedisConnection() {
    RedisOptions options = new RedisOptions()
      .setHost(settings.getRedisHost())
      .setPort(settings.getRedisPort())
      .setAuth(settings.getRedisPassword())
      .setSelect(1);
    return RedisClient.create(vertx, options);

  }


  @Override
  public <T> List<T> getMetrics(MetricGroupType metricGroupType, Class<T[]> clazz) {
    List<T> result = new ArrayList<>();
    final CountDownLatch latch = new CountDownLatch(1);
    try {
      RedisClient redisClient = getRedisConnection();
      redisClient.get(metricGroupType.name(), responseAsyncResult -> {
        if (responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
          T[] cachedEntities = Json.decodeValue(responseAsyncResult.result(), clazz);
          result.addAll(Arrays.asList(cachedEntities));
          redisClient.close(handler ->{});
          latch.countDown();
        }
      });
      latch.await();
    } catch (Exception ex) {
      LOG.error("Error fetching collection from Redis");
    }
    return result;
  }

  private List<GroupedRankingEntry> getMetrics(final MetricGroupType metricGroupType, final String filter) {
    List<GroupedRankingEntry> preFilteredMetrics = this.getMetrics(metricGroupType, GroupedRankingEntry[].class);
    return preFilteredMetrics.stream()
      .filter(entry -> entry.getKey().equals(filter))
      .collect(Collectors.toList());
  }

  @Override
  public MetricResponseWrapper searchMetrics(SearchFilter searchFilter) {
    Objects.requireNonNull(searchFilter, "Search filter is mandatory");
    MetricResponseWrapperBuilder responseBuilder = MetricResponseWrapper.builder();
    switch (searchFilter.getType()) {
      case URL: {
        responseBuilder
          .rankingEntrie(this.getMetrics(MetricGroupType.GROUP_BY_URL, RankingEntry[].class));
        break;
      }
      case REGION: {
        responseBuilder
          .groupedRankingEntries(this.getMetrics(MetricGroupType.GROUP_BY_REGION, GroupedRankingEntry[].class));
        break;
      }
      case MINUTE: {
        responseBuilder
          .rankingEntrie(this.getMetrics(MetricGroupType.GROUP_BY_MINUTE, RankingEntry[].class));
        break;
      }
      case DATE: {
        this.searchMetricsByDate(searchFilter, responseBuilder);
        break;
      }
    }
    return this.limitAndOrderResponse(searchFilter, responseBuilder.build());
  }

  @Override
  public void save(MetricGroupType metricGroupType, GroupedRankingEntry entry) {

    final Set<GroupedRankingEntry> rankingToBeCached = new HashSet<>();
    rankingToBeCached.add(entry);
    RedisClient redisClient = this.getRedisConnection();
    redisClient.get(metricGroupType.name(), responseAsyncResult -> {
      if (responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
        GroupedRankingEntry[] currentRankingEntries = Json.decodeValue(responseAsyncResult.result(),
          GroupedRankingEntry[].class);
        rankingToBeCached.addAll(Arrays.asList(currentRankingEntries));
      }
      redisClient.set(
        metricGroupType.name(),
        Json.encodePrettily(rankingToBeCached),
        res -> this.handleRedisUpdate(res, redisClient));
    });
  }

  @Override
  public void save(MetricGroupType metricGroupType, RankingEntry entry) {
    final Set<RankingEntry> rankingToBeCached = new HashSet<>();
    rankingToBeCached.add(entry);
    RedisClient redisClient = getRedisConnection();
    redisClient.get(metricGroupType.name(), responseAsyncResult -> {
      if (responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
        RankingEntry[] currentRankingEntries = Json.decodeValue(responseAsyncResult.result(),
          RankingEntry[].class);
        rankingToBeCached.addAll(Arrays.asList(currentRankingEntries));
      }
      redisClient.set(
        metricGroupType.name(),
        Json.encodePrettily(rankingToBeCached),
        res -> this.handleRedisUpdate(res, redisClient));
    });
  }

  @Override
  public ComponentState getRedisConnectionState() {
    final CountDownLatch latch = new CountDownLatch(1);
    final ComponentState state = ComponentState.off();
    try {
      RedisClient redisClient = getRedisConnection();
      redisClient.ping(responseAsyncResult -> {
        if (responseAsyncResult.succeeded()) {
          state.setWorking(true);
          redisClient.close(handler ->{});
        }
        latch.countDown();
      });
      latch.await(2, TimeUnit.SECONDS);
    } catch (Exception ex) {
      LOG.error("Error checking Redis connection");
    }
    return state;
  }

  private MetricResponseWrapper limitAndOrderResponse(SearchFilter searchFilter,
                                                      MetricResponseWrapper responseWrapper) {
    if (responseWrapper.getGroupedRankingEntries() != null &&
      !responseWrapper.getGroupedRankingEntries().isEmpty()) {
      responseWrapper.getGroupedRankingEntries().forEach(elem ->
        elem.setRanking(this.applyOrderAndLimit(elem.getRanking(), searchFilter))
      );
    }
    if (responseWrapper.getRankingEntries() != null &&
      !responseWrapper.getRankingEntries().isEmpty()) {
      responseWrapper.setRankingEntries(
        this.applyOrderAndLimit(responseWrapper.getRankingEntries(), searchFilter)
      );
    }
    return responseWrapper;
  }

  private List<RankingEntry> applyOrderAndLimit(List<RankingEntry> ranking,
                                                SearchFilter searchFilter) {
    Stream<RankingEntry> stream = ranking
      .stream()
      .sorted();
    if (searchFilter.getOrder() == SearchOrder.TOP)
      stream = stream.collect(reverse());
    return stream.limit(searchFilter.getSize()).collect(Collectors.toList());
  }

  private Collector<RankingEntry, ?, Stream<RankingEntry>> reverse() {
    return Collectors.collectingAndThen(Collectors.toList(), list -> {
      Collections.reverse(list);
      return list.stream();
    });
  }

  private void searchMetricsByDate(SearchFilter searchFilter, MetricResponseWrapperBuilder responseBuilder) {
    if (searchFilter.getDay() != null && !searchFilter.getDay().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_DAY, searchFilter.getDay())
      );
    else if (searchFilter.getWeek() != null && !searchFilter.getWeek().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_WEEK, searchFilter.getWeek())
      );

    else if (searchFilter.getMonth() != null && !searchFilter.getMonth().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_MONTH, searchFilter.getMonth())
      );

    else if (searchFilter.getYear() != null && !searchFilter.getYear().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_YEAR, searchFilter.getYear())
      );

  }

  private void handleRedisUpdate(AsyncResult<Void> voidAsyncResult, RedisClient redisClient) {
    if (voidAsyncResult.succeeded()) {
      LOG.debug("Metric update saved successfully on Redis ");
      redisClient.close(handler ->{});
    } else {
      LOG.debug("Error saving the metric update on Redis ");
    }
  }
}
