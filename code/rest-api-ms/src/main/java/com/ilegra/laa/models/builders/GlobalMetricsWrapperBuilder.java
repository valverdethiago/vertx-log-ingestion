package com.ilegra.laa.models.builders;

import com.ilegra.laa.models.GlobalMetricsWrapper;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;

import java.util.Set;

public class GlobalMetricsWrapperBuilder {
    private Set<GroupedRankingEntry> rankingByDay;
    private Set<RankingEntry> rankingByMinute;
    private Set<GroupedRankingEntry> rankingByMonth;
    private Set<GroupedRankingEntry> rankingByRegion;
    private Set<RankingEntry> rankingByUrl;
    private Set<GroupedRankingEntry> rankingByWeek;
    private Set<GroupedRankingEntry> rankingByYear;

    public GlobalMetricsWrapperBuilder rankingByDay(Set<GroupedRankingEntry> rankingByDay) {
        this.rankingByDay = rankingByDay;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByMinute(Set<RankingEntry> rankingByMinute) {
        this.rankingByMinute = rankingByMinute;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByMonth(Set<GroupedRankingEntry> rankingByMonth) {
        this.rankingByMonth = rankingByMonth;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByRegion(Set<GroupedRankingEntry> rankingByRegion) {
        this.rankingByRegion = rankingByRegion;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByUrl(Set<RankingEntry> rankingByUrl) {
        this.rankingByUrl = rankingByUrl;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByWeek(Set<GroupedRankingEntry> rankingByWeek) {
        this.rankingByWeek = rankingByWeek;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByYear(Set<GroupedRankingEntry> rankingByYear) {
        this.rankingByYear = rankingByYear;
        return this;
    }

    public GlobalMetricsWrapper build() {
        return new GlobalMetricsWrapper(rankingByDay,
          rankingByMinute,
          rankingByMonth,
          rankingByRegion,
          rankingByUrl,
          rankingByWeek,
          rankingByYear);
    }
}
