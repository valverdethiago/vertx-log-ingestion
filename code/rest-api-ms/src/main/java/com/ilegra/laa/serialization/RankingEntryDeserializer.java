package com.ilegra.laa.serialization;

import com.ilegra.laa.models.ranking.RankingEntry;

public class RankingEntryDeserializer extends JsonPojoDeserializer<RankingEntry> {
  public RankingEntryDeserializer() {
    super(RankingEntry.class);
  }
}
