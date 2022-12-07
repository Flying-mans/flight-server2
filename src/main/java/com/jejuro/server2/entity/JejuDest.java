package com.jejuro.server2.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
public class JejuDest {
    private ArrayList<FlightDetail> flightDetail;
}
