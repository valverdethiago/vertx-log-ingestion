package com.ilegra.laa.models.builders;

import com.ilegra.laa.models.AwsRegion;
import com.ilegra.laa.models.LogEntry;

import java.time.Instant;
import java.util.UUID;

public class LogEntryBuilder {
    private UUID id;
    private String url;
    private Instant date;
    private String clientId;
    private AwsRegion region;

    public LogEntryBuilder setId(UUID id) {
      this.id = id;
      return this;
    }

    public LogEntryBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public LogEntryBuilder setDate(Instant date) {
        this.date = date;
        return this;
    }

    public LogEntryBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public LogEntryBuilder setRegion(AwsRegion region) {
        this.region = region;
        return this;
    }

    public LogEntry createLogRequest() {
        return new LogEntry(id, url, date, clientId, region);
    }
}
