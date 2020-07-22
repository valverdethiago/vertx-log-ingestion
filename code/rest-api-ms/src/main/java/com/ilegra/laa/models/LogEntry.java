package com.ilegra.laa.models;

import com.ilegra.laa.builders.LogEntryBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

public class LogEntry implements Serializable {

  private UUID id;
  private String url;
  private Instant date;
  private String clientId;
  private AwsRegion region;

  public LogEntry() {
  }

  public LogEntry(UUID id, String url, Instant date, String clientId, AwsRegion region) {
    this();
    this.id = id;
    this.url = url;
    this.date = date;
    this.clientId = clientId;
    this.region = region;
  }

  public static LogEntryBuilder builder() {
    return new LogEntryBuilder();
  }

  public UUID getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public Instant getDate() {
    return date;
  }

  public String getClientId() {
    return clientId;
  }

  public AwsRegion getRegion() {
    return region;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LogEntry.class.getSimpleName() + "[", "]")
      .add("id='" + id + "'")
      .add("url='" + url + "'")
      .add("date=" + date)
      .add("clientId='" + clientId + "'")
      .add("region=" + region)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LogEntry logEntry = (LogEntry) o;
    return Objects.equals(id, logEntry.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
