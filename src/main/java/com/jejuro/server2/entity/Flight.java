package com.jejuro.server2.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Builder
@Document(collection = "flight")
public class Flight {

    @Id
    private String _id;

    private String date;

    private ArrayList<FlightInfo> info;
}
