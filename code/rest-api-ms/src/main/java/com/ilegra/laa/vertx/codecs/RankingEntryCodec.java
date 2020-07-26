package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.ranking.RankingEntry;

/**
 * Vert.x code for (de)serialization of @{@link RankingEntry}
 *
 * @author valverde.thiago
 */
public class RankingEntryCodec extends  JsonPojoCodec<RankingEntry> {

  public RankingEntryCodec() {
    super(RankingEntry.class);
  }

}
