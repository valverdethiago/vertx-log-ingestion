package com.ilegra.laa.models.ranking;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Wrapper for any ranked value such as url and minute
 *
 *  @author valverde.thiago
 */
public class RankingEntry implements Serializable, Comparable<RankingEntry> {
  private String key;
  private Long count;

  public RankingEntry() {
  }

  public RankingEntry(String key, Long count) {
    this.key = key;
    this.count = count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RankingEntry that = (RankingEntry) o;
    return key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", RankingEntry.class.getSimpleName() + "[", "]")
      .add("url='" + key + "'")
      .add("count=" + count)
      .toString();
  }

  @Override
  public int compareTo(RankingEntry rankingEntry) {
    return this.count.compareTo(rankingEntry.count);
  }

  public String getKey() {
    return key;
  }

  public Long getCount() {
    return count;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
