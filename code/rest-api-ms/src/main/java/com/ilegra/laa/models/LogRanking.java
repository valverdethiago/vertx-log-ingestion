package com.ilegra.laa.models;

import java.io.Serializable;
import java.util.StringJoiner;

public class LogRanking implements Serializable {
  private String url;
  private Long count;

  public LogRanking() {
  }

  public LogRanking(String url, Long count) {
    this.url = url;
    this.count = count;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LogRanking.class.getSimpleName() + "[", "]")
      .add("url='" + url + "'")
      .add("count=" + count)
      .toString();
  }

  public String getUrl() {
    return url;
  }

  public Long getCount() {
    return count;
  }
}
