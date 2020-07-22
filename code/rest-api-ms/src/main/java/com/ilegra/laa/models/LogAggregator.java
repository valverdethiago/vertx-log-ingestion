package com.ilegra.laa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class LogAggregator {
  private Set<LogEntry> urls = new HashSet<>();
  private Set<LogRanking> ranking = new HashSet<>();

  public Set<LogEntry> getUrls() {
    return urls;
  }

  public Set<LogRanking> getRanking() {
    return ranking;
  }

  public void generateRanking() {
    if(urls == null || urls.isEmpty()) {
      return;
    }
    this.ranking =
      urls.stream()
        .collect(Collectors.groupingBy(LogEntry::getUrl, Collectors.counting()))
        .entrySet().stream().map(entry -> new LogRanking(entry.getKey(), entry.getValue()))
    .collect(Collectors.toSet());
    this.urls = null;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LogAggregator.class.getSimpleName() + "[", "]")
      .add("urls=" + urls)
      .add("ranking=" + ranking)
      .toString();
  }
}
