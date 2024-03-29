package com.ilegra.laa.models.search;

import com.ilegra.laa.models.exceptions.ValidationException;

import java.util.Arrays;

/**
 * Represents all the possible values for type parameter at /metrics endpoint
 *
 * @author valverde.thiago
 */
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
