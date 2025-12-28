package com.h_me.carsapp;

import com.h_me.carsapp.dao.DealershipDAO;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Dealerships;
import com.h_me.carsapp.model.Vehicle;

import java.util.List;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Database Test...");

        // --------------------------------------------------
        // TEST 1: Dealerships
        // --------------------------------------------------
        System.out.println("\n--- Testing DealershipDAO ---");
        DealershipDAO dealershipDAO = new DealershipDAO();
        List<Dealerships> allDealerships = dealershipDAO.getAllDealerships();

        if (allDealerships.isEmpty()) {
            System.out.println("⚠️ No dealerships found. (Table might be empty)");
        } else {
            for (Dealerships d : allDealerships) {
                System.out.println("📍 Found Dealership: " + d.getName() + " in " + d.getCity());
            }
        }

        // --------------------------------------------------
        // TEST 2: Vehicles
        // --------------------------------------------------
        System.out.println("\n--- Testing VehicleDAO ---");
        VehicleDAO vehicleDAO = new VehicleDAO();

        // OPTIONAL: Uncomment this line once to add a test car if your DB is empty
        // addDummyCar(vehicleDAO);

        List<Vehicle> allCars = vehicleDAO.getAllAvailableVehicles();

        if (allCars.isEmpty()) {
            System.out.println("⚠️ No vehicles found. (Table might be empty)");
        } else {
            for (Vehicle v : allCars) {
                System.out.println("🚗 Found Car: " + v.getName() + " (" + v.getCategory() + ") - " + v.getPriceRental() + " MAD/day");
            }
        }

        System.out.println("\n✅ Test Finished!");
    }

    // Helper method to insert a fake car for testing
    private static void addDummyCar(VehicleDAO dao) {
        System.out.println("... Attempting to add a test car ...");
        Vehicle v = new Vehicle();
        v.setName("Test Peugeot 208");
        v.setCategory("Compact");
        v.setPricePurchase(150000);
        v.setPriceRental(300);
        v.setDealershipID(1);   // Make sure Dealership ID 1 exists in DB first!
        v.setManufactureID(1);  // Make sure Manufacturer ID 1 exists in DB first!

        dao.addVehicle(v);
    }
}