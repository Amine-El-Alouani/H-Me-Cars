package com.h_me.flightapp.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private String idBooking;
    private String userId;
    private String flightNum;
    private String passengerName;
    private String seatNumber;
    private double totalPrice;
    private String bookingDate;
    private String email;
    private String phoneNumber;
}
