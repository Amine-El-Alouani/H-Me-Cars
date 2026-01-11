package com.h_me.carsapp.utils;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Detects car brand from vehicle name and provides brand logo URLs.
 */
public class CarBrandDetector {
    
    // Map of brand keywords to logo URLs (using free CDN logos)
    private static final Map<String, String> BRAND_LOGOS = new HashMap<>();
    
    static {
        // Popular car brands with their logo URLs from logo.clearbit.com (free API)
        BRAND_LOGOS.put("toyota", "https://logo.clearbit.com/toyota.com");
        BRAND_LOGOS.put("honda", "https://logo.clearbit.com/honda.com");
        BRAND_LOGOS.put("ford", "https://logo.clearbit.com/ford.com");
        BRAND_LOGOS.put("chevrolet", "https://logo.clearbit.com/chevrolet.com");
        BRAND_LOGOS.put("chevy", "https://logo.clearbit.com/chevrolet.com");
        BRAND_LOGOS.put("bmw", "https://logo.clearbit.com/bmw.com");
        BRAND_LOGOS.put("mercedes", "https://logo.clearbit.com/mercedes-benz.com");
        BRAND_LOGOS.put("benz", "https://logo.clearbit.com/mercedes-benz.com");
        BRAND_LOGOS.put("audi", "https://logo.clearbit.com/audi.com");
        BRAND_LOGOS.put("volkswagen", "https://logo.clearbit.com/volkswagen.com");
        BRAND_LOGOS.put("vw", "https://logo.clearbit.com/volkswagen.com");
        BRAND_LOGOS.put("nissan", "https://logo.clearbit.com/nissan.com");
        BRAND_LOGOS.put("hyundai", "https://logo.clearbit.com/hyundai.com");
        BRAND_LOGOS.put("kia", "https://logo.clearbit.com/kia.com");
        BRAND_LOGOS.put("mazda", "https://logo.clearbit.com/mazda.com");
        BRAND_LOGOS.put("subaru", "https://logo.clearbit.com/subaru.com");
        BRAND_LOGOS.put("lexus", "https://logo.clearbit.com/lexus.com");
        BRAND_LOGOS.put("porsche", "https://logo.clearbit.com/porsche.com");
        BRAND_LOGOS.put("ferrari", "https://logo.clearbit.com/ferrari.com");
        BRAND_LOGOS.put("lamborghini", "https://logo.clearbit.com/lamborghini.com");
        BRAND_LOGOS.put("tesla", "https://logo.clearbit.com/tesla.com");
        BRAND_LOGOS.put("volvo", "https://logo.clearbit.com/volvo.com");
        BRAND_LOGOS.put("jaguar", "https://logo.clearbit.com/jaguar.com");
        BRAND_LOGOS.put("land rover", "https://logo.clearbit.com/landrover.com");
        BRAND_LOGOS.put("landrover", "https://logo.clearbit.com/landrover.com");
        BRAND_LOGOS.put("range rover", "https://logo.clearbit.com/landrover.com");
        BRAND_LOGOS.put("jeep", "https://logo.clearbit.com/jeep.com");
        BRAND_LOGOS.put("dodge", "https://logo.clearbit.com/dodge.com");
        BRAND_LOGOS.put("chrysler", "https://logo.clearbit.com/chrysler.com");
        BRAND_LOGOS.put("ram", "https://logo.clearbit.com/ramtrucks.com");
        BRAND_LOGOS.put("fiat", "https://logo.clearbit.com/fiat.com");
        BRAND_LOGOS.put("alfa romeo", "https://logo.clearbit.com/alfaromeo.com");
        BRAND_LOGOS.put("maserati", "https://logo.clearbit.com/maserati.com");
        BRAND_LOGOS.put("bentley", "https://logo.clearbit.com/bentley.com");
        BRAND_LOGOS.put("rolls royce", "https://logo.clearbit.com/rolls-roycemotorcars.com");
        BRAND_LOGOS.put("aston martin", "https://logo.clearbit.com/astonmartin.com");
        BRAND_LOGOS.put("mclaren", "https://logo.clearbit.com/mclaren.com");
        BRAND_LOGOS.put("bugatti", "https://logo.clearbit.com/bugatti.com");
        BRAND_LOGOS.put("peugeot", "https://logo.clearbit.com/peugeot.com");
        BRAND_LOGOS.put("renault", "https://logo.clearbit.com/renault.com");
        BRAND_LOGOS.put("citroen", "https://logo.clearbit.com/citroen.com");
        BRAND_LOGOS.put("dacia", "https://logo.clearbit.com/dacia.com");
        BRAND_LOGOS.put("seat", "https://logo.clearbit.com/seat.com");
        BRAND_LOGOS.put("skoda", "https://logo.clearbit.com/skoda.com");
        BRAND_LOGOS.put("mini", "https://logo.clearbit.com/mini.com");
        BRAND_LOGOS.put("infiniti", "https://logo.clearbit.com/infiniti.com");
        BRAND_LOGOS.put("acura", "https://logo.clearbit.com/acura.com");
        BRAND_LOGOS.put("genesis", "https://logo.clearbit.com/genesis.com");
        BRAND_LOGOS.put("cadillac", "https://logo.clearbit.com/cadillac.com");
        BRAND_LOGOS.put("buick", "https://logo.clearbit.com/buick.com");
        BRAND_LOGOS.put("gmc", "https://logo.clearbit.com/gmc.com");
        BRAND_LOGOS.put("lincoln", "https://logo.clearbit.com/lincoln.com");
        BRAND_LOGOS.put("mitsubishi", "https://logo.clearbit.com/mitsubishi.com");
        BRAND_LOGOS.put("suzuki", "https://logo.clearbit.com/suzuki.com");
        BRAND_LOGOS.put("isuzu", "https://logo.clearbit.com/isuzu.com");
        // Moroccan popular brands
        BRAND_LOGOS.put("dacia", "https://logo.clearbit.com/dacia.com");
        BRAND_LOGOS.put("renault", "https://logo.clearbit.com/renault.com");
    }
    
    // Cache for loaded images
    private static final Map<String, Image> logoCache = new HashMap<>();
    
    /**
     * Detect brand from vehicle name and return logo URL
     */
    public static String detectBrandLogoUrl(String vehicleName) {
        if (vehicleName == null || vehicleName.isEmpty()) return null;
        
        String lowerName = vehicleName.toLowerCase();
        
        for (Map.Entry<String, String> entry : BRAND_LOGOS.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * Get brand logo as JavaFX Image (with caching)
     */
    public static Image getBrandLogo(String vehicleName, double width, double height) {
        String url = detectBrandLogoUrl(vehicleName);
        if (url == null) return null;
        
        // Check cache first
        if (logoCache.containsKey(url)) {
            Image cached = logoCache.get(url);
            // Return null if cached image had an error
            if (cached == null || cached.isError()) return null;
            return cached;
        }
        
        try {
            // Load from URL synchronously (backgroundLoading=false)
            Image logo = new Image(url, width, height, true, true, false);
            
            // Check if loading failed
            if (logo.isError()) {
                System.out.println("Failed to load brand logo from: " + url);
                logoCache.put(url, null); // Cache null to avoid retrying
                return null;
            }
            
            logoCache.put(url, logo);
            return logo;
        } catch (Exception e) {
            System.out.println("Could not load brand logo: " + e.getMessage());
            logoCache.put(url, null); // Cache null to avoid retrying
            return null;
        }
    }
    
    /**
     * Get brand name from vehicle name
     */
    public static String detectBrand(String vehicleName) {
        if (vehicleName == null || vehicleName.isEmpty()) return null;
        
        String lowerName = vehicleName.toLowerCase();
        
        for (String brand : BRAND_LOGOS.keySet()) {
            if (lowerName.contains(brand)) {
                // Return properly capitalized brand name
                return brand.substring(0, 1).toUpperCase() + brand.substring(1);
            }
        }
        
        return null;
    }
}
