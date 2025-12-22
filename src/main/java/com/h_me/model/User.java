package com.h_me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private String userID;
    private String firstName;
    private String lastName;
    private int phoneNum;
    private String email;
    private String password;
}
