package com.ilegra.laa.serialization;

import com.ilegra.laa.models.LogEntry;
import com.ilegra.laa.models.ranking.RankingEntry;

public class RankingEntrySerde extends JsonPojoSerde<RankingEntry>{
  public RankingEntrySerde() {
    super(RankingEntry.class);
  }
}
