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

    public boolean processRental(Vehicle vehicle, int userId, LocalDateTime startDate, LocalDateTime endDate) {

        // 1. NEW VALIDATION: Check Dates instead of just Status
        // If the status is 'MAINTENANCE', block it regardless of dates.
        if (vehicle.getStatus().equalsIgnoreCase("MAINTENANCE")) {
            System.out.println("Error: Car is under maintenance.");
            return false;
        }

        // Check if dates overlap with another reservation
        if (!reservationDAO.isCarAvailable(vehicle.getVehicleID(), startDate, endDate)) {
            System.out.println("Error: Car is already booked for these dates!");
            return false;
        }

        // 2. Process Calculation
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days < 1) days = 1;
        double totalCost = days * vehicle.getPriceRental();

        // 3. Create Object
        Reservation reservation = new Reservation();
        reservation.setTypeRes("RENTAL");
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotalCost((int) totalCost);
        reservation.setVehicleID(vehicle.getVehicleID());
        reservation.setUserID(userId);

        try {
            // 4. Save to DB
            reservationDAO.createReservation(reservation);

            // OPTIONAL: We don't necessarily need to set status to 'RENTED' anymore
            // because we rely on dates. But we can set it for visual reference if the rental is TODAY.
            if (startDate.toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                vehicleDAO.updateVehicleStatus(vehicle.getVehicleID(), "RENTED");
            }

            System.out.println("Rental Successful! Total Cost: " + totalCost);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}