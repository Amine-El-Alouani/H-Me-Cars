package com.h_me.flightapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Route {
    private String flightNum;
    private int airlineID;
    private int sourceID;
    private int destID;
    private String Equipement;
}
