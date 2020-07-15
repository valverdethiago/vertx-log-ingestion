package com.ilegra.laa.models;

import com.ilegra.laa.builders.LogRequestBuilder;

import java.time.Instant;

public class LogRequest {

  private String url;
  private Instant date;
  private String clientId;
  private AwsRegion region;

  public static LogRequestBuilder builder() {
    return new LogRequestBuilder();
  }

  public LogRequest(String url, Instant date, String clientId, AwsRegion region) {
    this.url = url;
    this.date = date;
    this.clientId = clientId;
    this.region = region;
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
    return "LogRequest{" +
      "url='" + url + '\'' +
      ", date=" + date +
      ", clientId='" + clientId + '\'' +
      ", region=" + region +
      '}';
  }


}
