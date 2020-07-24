package com.ilegra.laa.vertx.codecs;

import com.ilegra.laa.models.ranking.GroupedRankingEntry;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class GroupedRankingEntryCodec implements MessageCodec<GroupedRankingEntry, GroupedRankingEntry> {
  @Override
  public void encodeToWire(Buffer buffer, GroupedRankingEntry groupedRankingEntry) {
    String json = Json.encodePrettily(groupedRankingEntry);
    buffer.appendInt(json.getBytes().length);
    buffer.appendString(json);
  }

  @Override
  public GroupedRankingEntry decodeFromWire(int position, Buffer buffer) {
    return Json.decodeValue(buffer, GroupedRankingEntry.class);
  }

  @Override
  public GroupedRankingEntry transform(GroupedRankingEntry groupedRankingEntry) {
    return groupedRankingEntry;
  }

  @Override
  public String name() {
    return GroupedRankingEntryCodec.class.getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
