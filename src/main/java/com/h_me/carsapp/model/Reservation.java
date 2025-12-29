package com.h_me.carsapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Reservation {
    private int reservationID;
    private String typeRes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int totalCost;
    private String vehicleID;
    private int userID;

}