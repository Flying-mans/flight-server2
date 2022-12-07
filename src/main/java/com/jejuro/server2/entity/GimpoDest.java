package com.jejuro.server2.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
public class GimpoDest {
    private ArrayList<FlightDetail> flightDetail;
}
