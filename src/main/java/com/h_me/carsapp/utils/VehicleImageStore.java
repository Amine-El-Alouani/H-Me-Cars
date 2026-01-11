package com.h_me.carsapp.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Manages local storage for vehicle images.
 * Uses a Properties file to map vehicle names to image paths.
 */
public class VehicleImageStore {
    
    private static final Path STORE_DIR = Path.of(System.getProperty("user.home"), ".h-me-cars");
    private static final Path IMAGES_DIR = STORE_DIR.resolve("images");
    private static final Path CONFIG_FILE = STORE_DIR.resolve("vehicle_images.properties");
    
    private static Properties imageMap = new Properties();
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        try {
            Files.createDirectories(IMAGES_DIR);
            if (Files.exists(CONFIG_FILE)) {
                try (InputStream is = new FileInputStream(CONFIG_FILE.toFile())) {
                    imageMap.load(is);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load vehicle image config: " + e.getMessage());
            imageMap = new Properties();
        }
    }
    
    private static void saveConfig() {
        try {
            Files.createDirectories(STORE_DIR);
            try (OutputStream os = new FileOutputStream(CONFIG_FILE.toFile())) {
                imageMap.store(os, "Vehicle Image Mappings");
            }
        } catch (IOException e) {
            System.out.println("Could not save vehicle image config: " + e.getMessage());
        }
    }
    
    /**
     * Save an image path for a vehicle name
     */
    public static void saveImageForName(String vehicleName, String imagePath) {
        if (vehicleName == null || imagePath == null) return;
        // Use vehicle name as key (cleaned up for properties file)
        String key = vehicleName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        imageMap.setProperty(key, imagePath);
        saveConfig();
    }
    
    /**
     * Get image path by vehicle name
     */
    public static String getImageByName(String vehicleName) {
        if (vehicleName == null) return null;
        String key = vehicleName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        return imageMap.getProperty(key);
    }
    
    /**
     * Get the images directory path
     */
    public static Path getImagesDir() {
        return IMAGES_DIR;
    }
}
