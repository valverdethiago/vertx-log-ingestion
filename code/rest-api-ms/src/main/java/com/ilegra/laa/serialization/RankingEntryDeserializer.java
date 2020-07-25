package com.ilegra.laa.serialization;

import com.ilegra.laa.models.ranking.RankingEntry;

/**
 * Json Deserializer for @{@link RankingEntry}
 *
 * @author valverde.thiago
 */
public class RankingEntryDeserializer extends JsonPojoDeserializer<RankingEntry> {
  public RankingEntryDeserializer() {
    super(RankingEntry.class);
  }
}
