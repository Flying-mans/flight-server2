package com.jejuro.server2.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlightInfo {

    private String depDate;
    private JejuDest jejuDest;
    private GimpoDest gimpoDest;
}
