package com.ilegra.laa.serialization;

import com.ilegra.laa.models.ranking.GroupedRankingEntry;

public class GroupedRankingEntryDeserializer extends JsonPojoDeserializer<GroupedRankingEntry> {
  public GroupedRankingEntryDeserializer() {
    super(GroupedRankingEntry.class);
  }
}
