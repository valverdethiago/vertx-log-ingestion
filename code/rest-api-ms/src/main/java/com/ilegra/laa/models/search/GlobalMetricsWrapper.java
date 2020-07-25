package com.ilegra.laa.models.search;

import com.ilegra.laa.models.builders.GlobalMetricsWrapperBuilder;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;

import java.util.List;

/**
 * Wrapper for all metrics stored by the application
 *
 * @author valverde.thiago
 */
public class GlobalMetricsWrapper {
  private List<GroupedRankingEntry> rankingByDay;
  private List<RankingEntry> rankingByMinute;
  private List<GroupedRankingEntry> rankingByMonth;
  private List<GroupedRankingEntry> rankingByRegion;
  private List<RankingEntry> rankingByUrl;
  private List<GroupedRankingEntry> rankingByWeek;
  private List<GroupedRankingEntry> rankingByYear;

  public GlobalMetricsWrapper(List<GroupedRankingEntry> rankingByDay,
                              List<RankingEntry> rankingByMinute,
                              List<GroupedRankingEntry> rankingByMonth,
                              List<GroupedRankingEntry> rankingByRegion,
                              List<RankingEntry> rankingByUrl,
                              List<GroupedRankingEntry> rankingByWeek,
                              List<GroupedRankingEntry> rankingByYear) {
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

  public List<GroupedRankingEntry> getRankingByDay() {
    return rankingByDay;
  }

  public List<RankingEntry> getRankingByMinute() {
    return rankingByMinute;
  }

  public List<GroupedRankingEntry> getRankingByMonth() {
    return rankingByMonth;
  }

  public List<GroupedRankingEntry> getRankingByRegion() {
    return rankingByRegion;
  }

  public List<RankingEntry> getRankingByUrl() {
    return rankingByUrl;
  }

  public List<GroupedRankingEntry> getRankingByWeek() {
    return rankingByWeek;
  }

  public List<GroupedRankingEntry> getRankingByYear() {
    return rankingByYear;
  }
}
