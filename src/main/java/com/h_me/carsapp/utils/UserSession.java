package com.h_me.carsapp.utils;

import com.h_me.carsapp.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession(User user) {
        this.currentUser = user;
    }

    public static void setSession(User user) {
        instance = new UserSession(user);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public User getUser() {
        return currentUser;
    }

    public static void cleanUserSession() {
        instance = null;
    }
}