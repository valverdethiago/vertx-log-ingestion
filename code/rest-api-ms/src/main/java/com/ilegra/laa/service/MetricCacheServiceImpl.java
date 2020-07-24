package com.ilegra.laa.service;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.search.MetricResponseWrapper;
import com.ilegra.laa.models.search.SearchFilter;
import com.ilegra.laa.models.search.SearchOrder;
import com.ilegra.laa.models.builders.MetricResponseWrapperBuilder;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetricCacheServiceImpl implements MetricCacheService {

  private final static Logger LOG = LoggerFactory.getLogger(MetricCacheServiceImpl.class);

  protected final RedisClient redisClient;


  public MetricCacheServiceImpl() {
    RedisOptions options = new RedisOptions()
      .setHost("localhost")
      .setPort(6379)
      .setAuth("Illegra2020!")
      .setSelect(1);
    this.redisClient = RedisClient.create(Vertx.vertx(), options);
  }


  @Override
  public <T> List<T> getMetrics(MetricGroupType metricGroupType, Class<T[]> clazz) {
    List<T> result = new ArrayList<>();
    final CountDownLatch latch = new CountDownLatch(1);
    try {
      this.redisClient.get(metricGroupType.name(), responseAsyncResult -> {
        if (responseAsyncResult.succeeded() && responseAsyncResult.result() != null) {
          T[] cachedEntities = Json.decodeValue(responseAsyncResult.result(), clazz);
          result.addAll(Arrays.asList(cachedEntities));
          latch.countDown();
        }
      });
      latch.await();
    }
    catch (Exception ex) {
      LOG.error("Error fetching collection from Redis");
    }
    return result;
  }

  private List<GroupedRankingEntry> getMetrics(final MetricGroupType metricGroupType, final String filter) {
    List<GroupedRankingEntry> preFilteredMetrics = this.getMetrics(metricGroupType, GroupedRankingEntry[].class);
    return preFilteredMetrics.stream()
      .filter(entry-> entry.getKey().equals(filter))
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

  private MetricResponseWrapper limitAndOrderResponse(SearchFilter searchFilter,
                                                      MetricResponseWrapper responseWrapper) {
    if(responseWrapper.getGroupedRankingEntries() != null &&
      !responseWrapper.getGroupedRankingEntries().isEmpty()) {
      responseWrapper.getGroupedRankingEntries().forEach(elem ->
        elem.setRanking(this.applyOrderAndLimit(elem.getRanking(), searchFilter))
      );
    }
    if(responseWrapper.getRankingEntries() != null &&
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
    if(searchFilter.getOrder() == SearchOrder.TOP)
      stream = stream.collect(reverse());
    return stream.limit(searchFilter.getSize()).collect(Collectors.toList());
  }

  private Collector<RankingEntry, ? , Stream<RankingEntry>> reverse() {
    return Collectors.collectingAndThen(Collectors.toList(), list -> {
      Collections.reverse(list);
      return list.stream();
    });
  }

  private void searchMetricsByDate(SearchFilter searchFilter, MetricResponseWrapperBuilder responseBuilder) {
    if(searchFilter.getDay()!=null && !searchFilter.getDay().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_DAY, searchFilter.getDay())
      );
    else if(searchFilter.getWeek()!=null && !searchFilter.getWeek().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_WEEK, searchFilter.getWeek())
      );

    else if(searchFilter.getMonth()!=null && !searchFilter.getMonth().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_MONTH, searchFilter.getMonth())
      );

    else if(searchFilter.getYear()!=null && !searchFilter.getYear().isEmpty())
      responseBuilder.groupedRankingEntries(
        this.getMetrics(MetricGroupType.GROUP_BY_YEAR, searchFilter.getYear())
      );

  }
}
