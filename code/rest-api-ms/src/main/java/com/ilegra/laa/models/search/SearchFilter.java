package com.ilegra.laa.models.search;

import com.ilegra.laa.models.builders.SearchFilterBuilder;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Used to carry the filter parameters from the controller to the service
 *
 * @author valverde.thiago
 */
public class SearchFilter implements Serializable {

  private SearchOrder order;
  private SearchType type;
  private Long size;
  private String day;
  private String week;
  private String month;
  private String year;
  private String minute;

  public SearchFilter() {
  }

  public SearchFilter(SearchOrder order, SearchType type, Long size, String day, String week, String month, String year, String minute) {
    this.order = order;
    this.type = type;
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

  public SearchType getType() {
    return type;
  }

  public Long getSize() {
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
      .add("groupBy=" + type)
      .add("size=" + size)
      .add("day=" + day)
      .add("week='" + week + "'")
      .add("month=" + month)
      .add("year=" + year)
      .add("minute=" + minute)
      .toString();
  }
}
