package com.h_me.carsapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Manufacturers {
    private int manufactureID;
    private String name;
    private String country;
}