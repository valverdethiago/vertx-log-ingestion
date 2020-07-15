package com.ilegra.laa.builders;

import com.ilegra.laa.models.AwsRegion;
import com.ilegra.laa.models.LogRequest;

import java.time.LocalDateTime;

public class LogRequestBuilder {
    private String url;
    private LocalDateTime date;
    private String clientId;
    private AwsRegion region;

    public LogRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public LogRequestBuilder setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public LogRequestBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public LogRequestBuilder setRegion(AwsRegion region) {
        this.region = region;
        return this;
    }

    public LogRequest createLogRequest() {
        return new LogRequest(url, date, clientId, region);
    }
}
