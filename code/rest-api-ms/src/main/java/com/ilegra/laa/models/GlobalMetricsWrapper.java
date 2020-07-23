package com.ilegra.laa.models;

import com.ilegra.laa.models.builders.GlobalMetricsWrapperBuilder;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;

import java.util.Set;

public class GlobalMetricsWrapper {
  private Set<GroupedRankingEntry> rankingByDay;
  private Set<RankingEntry> rankingByMinute;
  private Set<GroupedRankingEntry> rankingByMonth;
  private Set<GroupedRankingEntry> rankingByRegion;
  private Set<RankingEntry> rankingByUrl;
  private Set<GroupedRankingEntry> rankingByWeek;
  private Set<GroupedRankingEntry> rankingByYear;

  public GlobalMetricsWrapper(Set<GroupedRankingEntry> rankingByDay,
                              Set<RankingEntry> rankingByMinute,
                              Set<GroupedRankingEntry> rankingByMonth,
                              Set<GroupedRankingEntry> rankingByRegion,
                              Set<RankingEntry> rankingByUrl,
                              Set<GroupedRankingEntry> rankingByWeek,
                              Set<GroupedRankingEntry> rankingByYear) {
    this.rankingByDay = rankingByDay;
    this.rankingByMinute = rankingByMinute;
    this.rankingByMonth = rankingByMonth;
    this.rankingByRegion = rankingByRegion;
    this.rankingByUrl = rankingByUrl;
    this.rankingByWeek = rankingByWeek;
    this.rankingByYear = rankingByYear;
  }

  public static GlobalMetricsWrapperBuilder builder() {
    return new GlobalMetricsWrapperBuilder();
  }

  public Set<GroupedRankingEntry> getRankingByDay() {
    return rankingByDay;
  }

  public Set<RankingEntry> getRankingByMinute() {
    return rankingByMinute;
  }

  public Set<GroupedRankingEntry> getRankingByMonth() {
    return rankingByMonth;
  }

  public Set<GroupedRankingEntry> getRankingByRegion() {
    return rankingByRegion;
  }

  public Set<RankingEntry> getRankingByUrl() {
    return rankingByUrl;
  }

  public Set<GroupedRankingEntry> getRankingByWeek() {
    return rankingByWeek;
  }

  public Set<GroupedRankingEntry> getRankingByYear() {
    return rankingByYear;
  }
}
