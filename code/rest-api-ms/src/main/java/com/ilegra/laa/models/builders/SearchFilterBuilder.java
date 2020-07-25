package com.ilegra.laa.models.builders;

import com.ilegra.laa.models.search.SearchFilter;
import com.ilegra.laa.models.search.SearchType;
import com.ilegra.laa.models.search.SearchOrder;

/**
 * Builder for @{@link SearchFilter}
 *
 * @author valverde.thiago
 */
public class SearchFilterBuilder {
  private SearchOrder order;
  private SearchType type;
  private Long size;
  private String day;
  private String week;
  private String month;
  private String year;
  private String minute;
  private String searchTerm;

  public SearchFilterBuilder order(SearchOrder order) {
    this.order = order;
    return this;
  }

  public SearchFilterBuilder type(SearchType type) {
    this.type = type;
    return this;
  }

  public SearchFilterBuilder size(Long size) {
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

  public SearchFilterBuilder minute(String minute) {
    this.minute = minute;
    return this;
  }

  public SearchFilterBuilder searchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
    return this;
  }

  public SearchFilter build() {
    return new SearchFilter(order, type, size, day, week, month, year, minute);
  }
}
