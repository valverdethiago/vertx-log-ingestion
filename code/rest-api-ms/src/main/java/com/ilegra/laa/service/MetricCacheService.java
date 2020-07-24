package com.ilegra.laa.service;

import com.ilegra.laa.models.MetricGroupType;
import com.ilegra.laa.models.MetricResponseWrapper;
import com.ilegra.laa.models.SearchFilter;

import java.util.List;

public interface MetricCacheService {

  <T> List<T> getMetrics(MetricGroupType metricGroupType, Class<T[]> clazz);

  MetricResponseWrapper searchMetrics(SearchFilter searchFilter);
}
