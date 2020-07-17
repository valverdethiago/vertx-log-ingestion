package com.ilegra.laa.models;

import com.ilegra.laa.builders.LogRequestBuilder;

import java.io.Serializable;
import java.time.Instant;

public class LogRequest implements Serializable {

  private String url;
  private Instant date;
  private String clientId;
  private AwsRegion region;

  public LogRequest() {
  }

  public LogRequest(String url, Instant date, String clientId, AwsRegion region) {
    this.url = url;
    this.date = date;
    this.clientId = clientId;
    this.region = region;
  }

  public static LogRequestBuilder builder() {
    return new LogRequestBuilder();
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
