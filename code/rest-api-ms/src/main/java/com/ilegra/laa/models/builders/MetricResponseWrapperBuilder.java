package com.ilegra.laa.models.builders;

import com.ilegra.laa.models.search.MetricResponseWrapper;
import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;

import java.util.List;

public class MetricResponseWrapperBuilder {
    private List<RankingEntry> rankingEntries;
    private List<GroupedRankingEntry> groupedRankingEntries;

    public MetricResponseWrapperBuilder rankingEntrie(List<RankingEntry> rankingEntrie) {
        this.rankingEntries = rankingEntrie;
        return this;
    }

    public MetricResponseWrapperBuilder groupedRankingEntries(List<GroupedRankingEntry> groupedRankingEntries) {
        this.groupedRankingEntries = groupedRankingEntries;
        return this;
    }

    public MetricResponseWrapper build() {
        return new MetricResponseWrapper(rankingEntries, groupedRankingEntries);
    }
}
