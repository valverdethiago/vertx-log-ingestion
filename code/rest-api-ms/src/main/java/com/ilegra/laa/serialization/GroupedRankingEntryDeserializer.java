package com.ilegra.laa.serialization;

import com.ilegra.laa.models.ranking.GroupedRankingEntry;

/**
 * Json Deserializer for @{@link GroupedRankingEntry}
 *
 * @author valverde.thiago
 */
public class GroupedRankingEntryDeserializer extends JsonPojoDeserializer<GroupedRankingEntry> {
  public GroupedRankingEntryDeserializer() {
    super(GroupedRankingEntry.class);
  }
}
