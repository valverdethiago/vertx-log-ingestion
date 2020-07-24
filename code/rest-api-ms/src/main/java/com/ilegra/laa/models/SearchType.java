package com.ilegra.laa.models;

import java.util.Arrays;

public enum SearchType {

  DATE, REGION, URL, MINUTE;

  public static SearchType from(String name) {
    return Arrays.stream(SearchType.values())
      .filter(searchGroupBy -> searchGroupBy.name().equalsIgnoreCase(name))
      .findFirst().orElseThrow(()->
        new ValidationException("Invalid value for search search type")
      );
  }
}
