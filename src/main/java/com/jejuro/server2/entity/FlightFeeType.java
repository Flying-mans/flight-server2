package com.jejuro.server2.entity;

public enum FlightFeeType {

    ECONOMY("economy"),
    BUSINESS("business"),
    DISCOUNT("discount");

    private String type;
    FlightFeeType(String type) {
        this.type = type;
    }

    public String getKey() {
        return type;
    }
}
