package com.ilegra.laa.builders;

import com.ilegra.laa.models.SearchFilter;
import com.ilegra.laa.models.SearchGroupBy;
import com.ilegra.laa.models.SearchOrder;

public class SearchFilterBuilder {
  private SearchOrder order;
  private SearchGroupBy groupBy;
  private Integer size;
  private String day;
  private String week;
  private String month;
  private String year;
  private String minute;

  public SearchFilterBuilder order(SearchOrder order) {
    this.order = order;
    return this;
  }

  public SearchFilterBuilder groupBy(SearchGroupBy groupBy) {
    this.groupBy = groupBy;
    return this;
  }

  public SearchFilterBuilder size(Integer size) {
    this.size = size;
    return this;
  }

  public SearchFilterBuilder day(String day) {
    this.day = day;
    return this;
  }

  public SearchFilterBuilder week(String week) {
    this.week = week;
    return this;
  }

  public SearchFilterBuilder month(String month) {
    this.month = month;
    return this;
  }

  public SearchFilterBuilder year(String year) {
    this.year = year;
    return this;
  }

  public SearchFilterBuilder minute(String year) {
    this.minute = minute;
    return this;
  }

  public SearchFilter createSearchFilter() {
    return new SearchFilter(order, groupBy, size, day, week, month, year, minute);
  }
}
