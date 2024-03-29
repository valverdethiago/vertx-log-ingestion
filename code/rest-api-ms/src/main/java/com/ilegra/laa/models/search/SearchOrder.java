package com.ilegra.laa.models.search;

import com.ilegra.laa.models.exceptions.ValidationException;

import java.util.Arrays;

/**
 * Represents all possible values for ordering a query on /metrics endpoint
 *
 * @author valverde.thiago
 */
public enum SearchOrder {

  TOP, DOWN;

  public static SearchOrder from(String name) {
    if (name == null || name.trim().isEmpty())
      return null;
    return Arrays.stream(SearchOrder.values())
      .filter(region -> region.name().equalsIgnoreCase(name))
      .findFirst().orElseThrow(() ->
        new ValidationException("Invalid value for search order")
      );
  }
}
