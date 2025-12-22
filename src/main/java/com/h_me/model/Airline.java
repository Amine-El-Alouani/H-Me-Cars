package com.h_me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class Airline {
    private int id;
    private String name;
    private String alias;
    private String iata;
    private String country;
    private boolean active;
}
