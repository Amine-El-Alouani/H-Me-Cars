package com.h_me.carsapp.utils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects car brand from vehicle name and creates brand badges.
 * Uses logo images from GitHub CDN (car-logos-dataset via jsDelivr).
 * Falls back to text badges if image loading fails.
 */
public class CarBrandDetector {
    
    // Brand info: logo filename (from car-logos-dataset), abbreviation, color
    private static final Map<String, String[]> BRANDS = new HashMap<>();
    
    // Cache for loaded logos
    private static final Map<String, Image> logoCache = new HashMap<>();
    private static final Map<String, Boolean> failedLogos = new HashMap<>();
    
    // CDN base URL for car logos (from filippofilip95/car-logos-dataset)
    private static final String LOGO_CDN = "https://raw.githubusercontent.com/filippofilip95/car-logos-dataset/master/logos/thumb/";
    
    static {
        // Format: brand keyword -> [logo filename, abbreviation, hex color]
        BRANDS.put("toyota", new String[]{"toyota.png", "T", "#EB0A1E"});
        BRANDS.put("honda", new String[]{"honda.png", "H", "#CC0000"});
        BRANDS.put("ford", new String[]{"ford.png", "F", "#003478"});
        BRANDS.put("chevrolet", new String[]{"chevrolet.png", "C", "#D4AA00"});
        BRANDS.put("chevy", new String[]{"chevrolet.png", "C", "#D4AA00"});
        BRANDS.put("bmw", new String[]{"bmw.png", "B", "#0066B1"});
        BRANDS.put("mercedes", new String[]{"mercedes-benz.png", "M", "#00ADEF"});
        BRANDS.put("benz", new String[]{"mercedes-benz.png", "M", "#00ADEF"});
        BRANDS.put("audi", new String[]{"audi.png", "A", "#BB0A30"});
        BRANDS.put("volkswagen", new String[]{"volkswagen.png", "VW", "#001E50"});
        BRANDS.put("vw", new String[]{"volkswagen.png", "VW", "#001E50"});
        BRANDS.put("nissan", new String[]{"nissan.png", "N", "#C3002F"});
        BRANDS.put("hyundai", new String[]{"hyundai.png", "H", "#002C5F"});
        BRANDS.put("kia", new String[]{"kia.png", "K", "#05141F"});
        BRANDS.put("mazda", new String[]{"mazda.png", "M", "#101010"});
        BRANDS.put("subaru", new String[]{"subaru.png", "S", "#013C91"});
        BRANDS.put("lexus", new String[]{"lexus.png", "L", "#1A1A1A"});
        BRANDS.put("porsche", new String[]{"porsche.png", "P", "#B12B28"});
        BRANDS.put("ferrari", new String[]{"ferrari.png", "F", "#FF2800"});
        BRANDS.put("lamborghini", new String[]{"lamborghini.png", "L", "#DDB321"});
        BRANDS.put("tesla", new String[]{"tesla.png", "T", "#CC0000"});
        BRANDS.put("volvo", new String[]{"volvo.png", "V", "#003057"});
        BRANDS.put("jaguar", new String[]{"jaguar.png", "J", "#1A472A"});
        BRANDS.put("jeep", new String[]{"jeep.png", "J", "#3A5A40"});
        BRANDS.put("dodge", new String[]{"dodge.png", "D", "#BA0C2F"});
        BRANDS.put("ram", new String[]{"ram.png", "R", "#000000"});
        BRANDS.put("fiat", new String[]{"fiat.png", "F", "#9B1B30"});
        BRANDS.put("maserati", new String[]{"maserati.png", "M", "#0C2340"});
        BRANDS.put("bentley", new String[]{"bentley.png", "B", "#333333"});
        BRANDS.put("peugeot", new String[]{"peugeot.png", "P", "#003B7C"});
        BRANDS.put("renault", new String[]{"renault.png", "R", "#FFCC00"});
        BRANDS.put("citroen", new String[]{"citroen.png", "C", "#AC1E2D"});
        BRANDS.put("dacia", new String[]{"dacia.png", "D", "#003B7C"});
        BRANDS.put("seat", new String[]{"seat.png", "S", "#B4000E"});
        BRANDS.put("skoda", new String[]{"skoda.png", "S", "#4BA82E"});
        BRANDS.put("mini", new String[]{"mini.png", "M", "#000000"});
        BRANDS.put("infiniti", new String[]{"infiniti.png", "I", "#1C1C1C"});
        BRANDS.put("acura", new String[]{"acura.png", "A", "#1C1C1C"});
        BRANDS.put("genesis", new String[]{"genesis.png", "G", "#8B634B"});
        BRANDS.put("cadillac", new String[]{"cadillac.png", "C", "#9E7B5C"});
        BRANDS.put("gmc", new String[]{"gmc.png", "G", "#CC0000"});
        BRANDS.put("lincoln", new String[]{"lincoln.png", "L", "#0C2340"});
        BRANDS.put("mitsubishi", new String[]{"mitsubishi.png", "M", "#E60012"});
        BRANDS.put("suzuki", new String[]{"suzuki.png", "S", "#E4002B"});
        BRANDS.put("land rover", new String[]{"land-rover.png", "LR", "#005A2B"});
        BRANDS.put("range rover", new String[]{"land-rover.png", "RR", "#005A2B"});
        BRANDS.put("alfa romeo", new String[]{"alfa-romeo.png", "AR", "#8B0000"});
        BRANDS.put("aston martin", new String[]{"aston-martin.png", "AM", "#006400"});
        BRANDS.put("bugatti", new String[]{"bugatti.png", "Bu", "#BE0030"});
        BRANDS.put("mclaren", new String[]{"mclaren.png", "Mc", "#FF6600"});
        BRANDS.put("rolls royce", new String[]{"rolls-royce.png", "RR", "#1C1C1C"});
    }
    
    /**
     * Create a brand badge for a vehicle name.
     * Tries to load logo from CDN, falls back to text badge.
     */
    public static Node createBrandBadge(String vehicleName) {
        if (vehicleName == null || vehicleName.isEmpty()) return null;
        
        String lowerName = vehicleName.toLowerCase();
        String[] brandInfo = null;
        
        for (Map.Entry<String, String[]> entry : BRANDS.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                brandInfo = entry.getValue();
                break;
            }
        }
        
        if (brandInfo == null) return null;
        
        String logoFilename = brandInfo[0];
        String logoUrl = LOGO_CDN + logoFilename;
        
        // Check if this logo already failed
        if (failedLogos.containsKey(logoUrl)) {
            return createTextBadge(brandInfo[1], brandInfo[2]);
        }
        
        // Check cache
        if (logoCache.containsKey(logoUrl)) {
            Image cached = logoCache.get(logoUrl);
            if (cached != null && !cached.isError()) {
                return createLogoBadge(cached);
            }
        }
        
        // Try to load logo from CDN
        try {
            Image logo = new Image(logoUrl, 24, 24, true, true, false);
            if (!logo.isError()) {
                logoCache.put(logoUrl, logo);
                return createLogoBadge(logo);
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Mark as failed and use text badge
        failedLogos.put(logoUrl, true);
        return createTextBadge(brandInfo[1], brandInfo[2]);
    }
    
    private static Node createLogoBadge(Image logo) {
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(20);
        logoView.setFitHeight(20);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        return logoView;
    }
    
    private static Node createTextBadge(String abbrev, String colorHex) {
        Circle circle = new Circle(10);
        try {
            circle.setFill(Color.web(colorHex));
        } catch (Exception e) {
            circle.setFill(Color.web("#6366f1"));
        }
        
        Label label = new Label(abbrev);
        label.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        StackPane badge = new StackPane(circle, label);
        badge.setMinSize(20, 20);
        badge.setMaxSize(20, 20);
        
        return badge;
    }
    
    /**
     * Check if vehicle has a known brand
     */
    public static boolean hasBrand(String vehicleName) {
        if (vehicleName == null) return false;
        String lowerName = vehicleName.toLowerCase();
        for (String brand : BRANDS.keySet()) {
            if (lowerName.contains(brand)) return true;
        }
        return false;
    }
}
