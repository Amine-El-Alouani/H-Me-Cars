package com.h_me.carsapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Vehicle {
    private int vehicleID;
    private String name;
    private String category;
    private double pricePurchase;
    private double priceRental;
    private String status;
    private int dealershipID;
    private int manufactureID;
    private java.time.LocalDateTime availableFrom;
    private String imagePath; // Path to car image file (local)
    private byte[] imageData; // Image bytes stored in database
}
