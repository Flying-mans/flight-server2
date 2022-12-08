package com.jejuro.server2.scheduler;

public enum InterParkKeyType {

    REPLY_AVAIL_FARE("replyAvailFare"),
    REPLY_AVAIL_FARE_RT("replyAvailFareRT");

    private String key;
    InterParkKeyType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
