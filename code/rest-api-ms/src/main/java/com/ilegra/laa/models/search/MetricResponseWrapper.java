package com.ilegra.laa.models.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ilegra.laa.models.builders.MetricResponseWrapperBuilder;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class MetricResponseWrapper {

  private List<RankingEntry> rankingEntries;
  private List<GroupedRankingEntry> groupedRankingEntries;

  public MetricResponseWrapper() {
  }

  public MetricResponseWrapper(List<RankingEntry> rankingEntries, List<GroupedRankingEntry> groupedRankingEntries) {
    this.rankingEntries = rankingEntries;
    this.groupedRankingEntries = groupedRankingEntries;
  }

  public static MetricResponseWrapperBuilder builder() {
    return new MetricResponseWrapperBuilder();
  }

  public List<RankingEntry> getRankingEntries() {
    return rankingEntries;
  }

  public List<GroupedRankingEntry> getGroupedRankingEntries() {
    return groupedRankingEntries;
  }

  public void setRankingEntries(List<RankingEntry> rankingEntries) {
    this.rankingEntries = rankingEntries;
  }

  public void setGroupedRankingEntries(List<GroupedRankingEntry> groupedRankingEntries) {
    this.groupedRankingEntries = groupedRankingEntries;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return (this.rankingEntries == null || this.rankingEntries.isEmpty())
      && (this.groupedRankingEntries == null || this.groupedRankingEntries.isEmpty());
  }
}
