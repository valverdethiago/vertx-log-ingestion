package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.ranking.RankingEntry;

public class RankingEntryCodec extends  JsonPojoCodec<RankingEntry> {

  public RankingEntryCodec() {
    super(RankingEntry.class);
  }

}
