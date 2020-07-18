package com.ilegra.laa.models;

import com.ilegra.laa.builders.SearchFilterBuilder;

import java.io.Serializable;

public class SearchFilter implements Serializable {

  private SearchOrder order;
  private SearchGroupBy groupBy ;
  private Integer size;

  public SearchFilter() {
  }

  public SearchFilter(SearchOrder order,
                      SearchGroupBy groupBy,
                      Integer size) {
    this.order = order;
    this.groupBy = groupBy;
    this.size = size;
  }

  public static SearchFilterBuilder builder() {
    return new SearchFilterBuilder();
  }

  public SearchOrder getOrder() {
    return order;
  }

  public SearchGroupBy getGroupBy() {
    return groupBy;
  }

  public Integer getSize() {
    return size;
  }

  @Override
  public String toString() {
    return "SearchFilter{" +
      "order=" + order +
      ", groupBy=" + groupBy +
      ", size='" + size + '\'' +
      '}';
  }
}
