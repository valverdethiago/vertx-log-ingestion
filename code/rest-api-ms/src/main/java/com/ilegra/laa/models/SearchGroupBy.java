package com.ilegra.laa.models;

import java.util.Arrays;
import java.util.Optional;

public enum SearchGroupBy {

  MINUTE, DAY, WEEK, MONTH, YEAR, REGION, URL;

  public static SearchGroupBy from(String name) {
    return Arrays.stream(SearchGroupBy.values())
      .filter(searchGroupBy -> searchGroupBy.name().equals(name))
      .findFirst().orElseThrow(()->
        new ValidationException("Invalid value for search groupyBy")
      );
  }
}
