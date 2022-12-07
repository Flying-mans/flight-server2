package com.jejuro.server2.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PriceDetail {

    FlightFeeType type;
    int fee;
}
