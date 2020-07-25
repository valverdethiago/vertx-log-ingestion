package com.ilegra.laa.models.builders;

import com.ilegra.laa.models.search.GlobalMetricsWrapper;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;

import java.util.List;

/**
 * Builder for @{@link GlobalMetricsWrapper}
 *
 * @author valverde.thiago
 */
public class GlobalMetricsWrapperBuilder {
    private List<GroupedRankingEntry> rankingByDay;
    private List<RankingEntry> rankingByMinute;
    private List<GroupedRankingEntry> rankingByMonth;
    private List<GroupedRankingEntry> rankingByRegion;
    private List<RankingEntry> rankingByUrl;
    private List<GroupedRankingEntry> rankingByWeek;
    private List<GroupedRankingEntry> rankingByYear;

    public GlobalMetricsWrapperBuilder rankingByDay(List<GroupedRankingEntry> rankingByDay) {
        this.rankingByDay = rankingByDay;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByMinute(List<RankingEntry> rankingByMinute) {
        this.rankingByMinute = rankingByMinute;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByMonth(List<GroupedRankingEntry> rankingByMonth) {
        this.rankingByMonth = rankingByMonth;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByRegion(List<GroupedRankingEntry> rankingByRegion) {
        this.rankingByRegion = rankingByRegion;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByUrl(List<RankingEntry> rankingByUrl) {
        this.rankingByUrl = rankingByUrl;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByWeek(List<GroupedRankingEntry> rankingByWeek) {
        this.rankingByWeek = rankingByWeek;
        return this;
    }

    public GlobalMetricsWrapperBuilder rankingByYear(List<GroupedRankingEntry> rankingByYear) {
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
