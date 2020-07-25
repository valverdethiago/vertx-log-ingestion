package com.ilegra.laa.models;

/**
 * Stores all possible patterns to apply to date parameters at /metrics endpoint
 *
 * @author valverde.thiago
 */
public enum DatePattern {

  MINUTE("yyyy-MM-dd HH-mm"),
  DAY("yyyy-MM-dd"),
  MONTH("yyyy-MM"),
  YEAR("yyyy");

  private String pattern;

  DatePattern(String pattern) {
    this.pattern = pattern;
  }

  public String getPattern() {
    return pattern;
  }
}
