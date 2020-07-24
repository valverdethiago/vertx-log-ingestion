package com.ilegra.laa.service;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.search.MetricResponseWrapper;
import com.ilegra.laa.models.search.SearchFilter;

import java.util.List;

public interface MetricCacheService {

  <T> List<T> getMetrics(MetricGroupType metricGroupType, Class<T[]> clazz);

  MetricResponseWrapper searchMetrics(SearchFilter searchFilter);

  void save(MetricGroupType metricGroupType, GroupedRankingEntry entry);
}
