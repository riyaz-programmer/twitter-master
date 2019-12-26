package com.tbb.data.twitter.core.model;

public final class TwitterHandler {

    private String consumerKey;
    private String consumerSecret;
    private String appId;
    private int remainingRate;

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getRemainingRate() {
        return remainingRate;
    }

    public void setRemainingRate(int remainingRate) {
        this.remainingRate = remainingRate;
    }
}
