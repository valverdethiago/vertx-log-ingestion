package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import com.ilegra.laa.models.ranking.RankingEntry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class RankingEntryCodec implements MessageCodec<RankingEntry, RankingEntry> {
  @Override
  public void encodeToWire(Buffer buffer, RankingEntry rankingEntry) {
    String json = Json.encodePrettily(rankingEntry);
    int length = json.getBytes().length;
    buffer.appendInt(length);
    buffer.appendString(json);
  }

  @Override
  public RankingEntry decodeFromWire(int position, Buffer buffer) {
    return Json.decodeValue(buffer, RankingEntry.class);
  }

  @Override
  public RankingEntry transform(RankingEntry rankingEntry) {
    return rankingEntry;
  }

  @Override
  public String name() {
    return RankingEntryCodec.class.getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
