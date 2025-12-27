package com.h_me.carsapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Vehicles {
    private int vehicleID;
    private String name;
    private String category;
    private double pricePurchase;
    private double priceRental;
    private String status;
}