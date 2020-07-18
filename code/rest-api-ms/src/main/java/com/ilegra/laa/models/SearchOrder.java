package com.ilegra.laa.models;

import java.util.Arrays;
import java.util.Optional;

public enum SearchOrder {

  TOP, DOWN;

  public static SearchOrder from(String name) {
    if(name == null || name.trim().isEmpty())
      return null;
    return Arrays.stream(SearchOrder.values())
      .filter(region -> region.name().equals(name))
      .findFirst().orElseThrow(()->
        new ValidationException("Invalid value for search order")
      );
  }
}
