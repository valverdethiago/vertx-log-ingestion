package com.ilegra.laa.models;

import com.ilegra.laa.builders.SearchFilterBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

public class SearchFilter implements Serializable {

  private SearchOrder order;
  private SearchGroupBy groupBy ;
  private Integer size;
  private String day;
  private String week;
  private String month;
  private String year;
  private String minute;

  public SearchFilter() {
  }

  public SearchFilter(SearchOrder order, SearchGroupBy groupBy, Integer size, String day, String week, String month, String year, String minute) {
    this.order = order;
    this.groupBy = groupBy;
    this.size = size;
    this.day = day;
    this.week = week;
    this.month = month;
    this.year = year;
    this.minute = minute;
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

  public String getDay() {
    return day;
  }

  public String getWeek() {
    return week;
  }

  public String getMonth() {
    return month;
  }

  public String getYear() {
    return year;
  }

  public String getMinute() {
    return minute;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SearchFilter.class.getSimpleName() + "[", "]")
      .add("order=" + order)
      .add("groupBy=" + groupBy)
      .add("size=" + size)
      .add("day=" + day)
      .add("week='" + week + "'")
      .add("month=" + month)
      .add("year=" + year)
      .add("minute=" + minute)
      .toString();
  }
}
