package com.h_me.carsapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Dealerships {
    private int dealershipID;
    private String name;
    private String city;
    private double latitude;
    private double longitude;
}