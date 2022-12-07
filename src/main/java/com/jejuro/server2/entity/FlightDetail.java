package com.jejuro.server2.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
public class FlightDetail {

    private String airCode;
    private String depTime;
    private String arrTime;
    private ArrayList<PriceDetail> priceDetails;
}
