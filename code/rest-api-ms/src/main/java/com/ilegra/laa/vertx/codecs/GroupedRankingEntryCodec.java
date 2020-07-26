package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.ranking.GroupedRankingEntry;

/**
 * Vert.x codec for (de)serializing @{@link GroupedRankingEntry}
 *
 * @author valverde.thiago
 */
public class GroupedRankingEntryCodec extends JsonPojoCodec<GroupedRankingEntry> {
  public GroupedRankingEntryCodec() {
    super(GroupedRankingEntry.class);
  }
}
