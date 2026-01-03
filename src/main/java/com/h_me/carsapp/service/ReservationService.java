package com.h_me.carsapp.service;

import com.h_me.carsapp.dao.ReservationDAO;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.model.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ReservationService {

    private ReservationDAO reservationDAO;
    private VehicleDAO vehicleDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.vehicleDAO = new VehicleDAO();
    }

    /**
     * FIX: Removed 'static' keyword.
     * Now this method can access 'reservationDAO' and 'vehicleDAO'.
     */
    public boolean processRental(Vehicle vehicle, int userId, LocalDateTime startDate, LocalDateTime endDate) {

        // 1. Validation check
        if (!vehicle.getStatus().equalsIgnoreCase("AVAILABLE")) {
            System.out.println("Error: This car is not available!");
            return false;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days < 1) days = 1; // Minimum 1 day rental

        double totalCost = days * vehicle.getPriceRental();

        Reservation reservation = new Reservation();
        reservation.setTypeRes("RENTAL");
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotalCost((int) totalCost);
        reservation.setVehicleID(vehicle.getVehicleID());
        reservation.setUserID(userId);

        try {
            reservationDAO.createReservation(reservation);
            vehicleDAO.updateVehicleStatus(vehicle.getVehicleID(), "RENTED");
            System.out.println("Rental Successful! Total Cost: " + totalCost + " MAD");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}