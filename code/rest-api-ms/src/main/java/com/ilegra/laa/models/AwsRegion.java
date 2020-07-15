package com.ilegra.laa.models;

import java.util.Arrays;
import java.util.Optional;

public enum AwsRegion {

  US_EAST_1(1, "us-east-1"),
  US_WEST_2(2, "us-west-2"),
  AP_SOUTH_1(3, "ap-south-1");

  private Integer code;
  private String name;

  AwsRegion(Integer code, String name) {
    this.code = code;
    this.name = name;
  }

  public static Optional<AwsRegion> from(Integer code) {
    return Arrays.stream(AwsRegion.values())
        .filter(region -> region.getCode().equals(code))
        .findFirst();
  }

  public static Optional<AwsRegion> from(String name) {
    return Arrays.stream(AwsRegion.values())
        .filter(region -> region.getName().equals(name))
        .findFirst();
  }

  public Integer getCode() {
    return code;
  }

  public String getName() {
    return name;
  }
}
