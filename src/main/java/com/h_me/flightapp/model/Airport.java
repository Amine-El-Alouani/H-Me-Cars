package com.h_me.flightapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Airport {
    private String AirportID;
    private String name;
    private String city;
    private String Country;
    private String iata;
    private double latitude;
    private double longtitude;
    private double altitude;
    private String timezone;
}
