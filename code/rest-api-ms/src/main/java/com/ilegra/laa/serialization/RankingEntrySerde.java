package com.ilegra.laa.serialization;

import com.ilegra.laa.models.ranking.RankingEntry;

/**
 * Serde for processing @{@link RankingEntry} json messages to and from kafka topics
 *
 * @author valverde.thiago
 */
public class RankingEntrySerde extends JsonPojoSerde<RankingEntry>{
  public RankingEntrySerde() {
    super(RankingEntry.class);
  }
}
