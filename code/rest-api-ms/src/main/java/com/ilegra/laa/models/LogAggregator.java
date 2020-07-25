package com.ilegra.laa.models;

import com.ilegra.laa.models.ranking.RankingEntry;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Intermediary class to store values grouped to send to kafka streams
 *
 * @author valverde.thiago
 */
public class LogAggregator implements Serializable {
  private Set<LogEntry> urls;
  private Set<RankingEntry> ranking;

  public LogAggregator() {
    this.urls = new HashSet<>();
    this.ranking = new TreeSet<>();
  }

  public Set<LogEntry> getUrls() {
    return urls;
  }

  public Set<RankingEntry> getRanking() {
    return ranking;
  }

  public LogAggregator add(LogEntry log) {
    this.urls.add(log);
    return this;
  }

  public void generateRanking() {
    if (urls == null || urls.isEmpty()) {
      return;
    }
    this.ranking =
      urls.stream()
        .collect(Collectors.groupingBy(LogEntry::getUrl, Collectors.counting()))
        .entrySet().stream().map(entry -> new RankingEntry(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  public Long countUrls() {
    return urls == null ? 0L : urls.size();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LogAggregator.class.getSimpleName() + "[", "]")
      .add("urls=" + urls)
      .add("ranking=" + ranking)
      .toString();
  }
}
