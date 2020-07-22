package com.ilegra.laa.models;

import java.util.Objects;

public class UrlCounter {
  private String url;
  private Long count;

  public UrlCounter() {
  }

  public UrlCounter(String url) {
    this.url = url;
    this.count = 0L;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UrlCounter that = (UrlCounter) o;
    return url.equals(that.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  public String getUrl() {
    return url;
  }

  public Long getCount() {
    return count;
  }
}
