package com.ilegra.laa.models.ranking;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class GroupedRankingEntry implements Serializable {
  private String key;
  private Set<RankingEntry> ranking;

  public GroupedRankingEntry() {
  }

  public GroupedRankingEntry(String key, Set<RankingEntry> ranking) {
    this.key = key;
    this.ranking = ranking;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GroupedRankingEntry that = (GroupedRankingEntry) o;
    return key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", GroupedRankingEntry.class.getSimpleName() + "[", "]")
      .add("key='" + key + "'")
      .add("ranking=" + ranking)
      .toString();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Set<RankingEntry> getRanking() {
    return ranking;
  }

  public void setRanking(Set<RankingEntry> ranking) {
    this.ranking = ranking;
  }
}
