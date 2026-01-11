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
 * Detects car brand from vehicle name and provides brand badges.
 * Tries to fetch logos from network, falls back to text badges.
 */
public class CarBrandDetector {
    
    // Map of brand keywords to their info: [abbreviation, hex color, logo URL]
    private static final Map<String, String[]> BRANDS = new HashMap<>();
    
    // Cache for loaded logo images
    private static final Map<String, Image> logoCache = new HashMap<>();
    // Cache for failed URLs (to avoid retrying)
    private static final Map<String, Boolean> failedUrls = new HashMap<>();
    
    static {
        // Format: brand keyword -> [abbreviation, hex color, logo domain]
        BRANDS.put("toyota", new String[]{"T", "#EB0A1E", "toyota.com"});
        BRANDS.put("honda", new String[]{"H", "#CC0000", "honda.com"});
        BRANDS.put("ford", new String[]{"F", "#003478", "ford.com"});
        BRANDS.put("chevrolet", new String[]{"Ch", "#D4AA00", "chevrolet.com"});
        BRANDS.put("chevy", new String[]{"Ch", "#D4AA00", "chevrolet.com"});
        BRANDS.put("bmw", new String[]{"B", "#0066B1", "bmw.com"});
        BRANDS.put("mercedes", new String[]{"M", "#00ADEF", "mercedes-benz.com"});
        BRANDS.put("benz", new String[]{"M", "#00ADEF", "mercedes-benz.com"});
        BRANDS.put("audi", new String[]{"A", "#BB0A30", "audi.com"});
        BRANDS.put("volkswagen", new String[]{"VW", "#001E50", "volkswagen.com"});
        BRANDS.put("vw", new String[]{"VW", "#001E50", "volkswagen.com"});
        BRANDS.put("nissan", new String[]{"N", "#C3002F", "nissan.com"});
        BRANDS.put("hyundai", new String[]{"H", "#002C5F", "hyundai.com"});
        BRANDS.put("kia", new String[]{"K", "#05141F", "kia.com"});
        BRANDS.put("mazda", new String[]{"M", "#101010", "mazda.com"});
        BRANDS.put("subaru", new String[]{"S", "#013C91", "subaru.com"});
        BRANDS.put("lexus", new String[]{"L", "#1A1A1A", "lexus.com"});
        BRANDS.put("porsche", new String[]{"P", "#B12B28", "porsche.com"});
        BRANDS.put("ferrari", new String[]{"Fe", "#FF2800", "ferrari.com"});
        BRANDS.put("lamborghini", new String[]{"L", "#DDB321", "lamborghini.com"});
        BRANDS.put("tesla", new String[]{"T", "#CC0000", "tesla.com"});
        BRANDS.put("volvo", new String[]{"V", "#003057", "volvo.com"});
        BRANDS.put("jaguar", new String[]{"J", "#1A472A", "jaguar.com"});
        BRANDS.put("jeep", new String[]{"Jp", "#3A5A40", "jeep.com"});
        BRANDS.put("dodge", new String[]{"D", "#BA0C2F", "dodge.com"});
        BRANDS.put("ram", new String[]{"R", "#000000", "ramtrucks.com"});
        BRANDS.put("fiat", new String[]{"Fi", "#9B1B30", "fiat.com"});
        BRANDS.put("maserati", new String[]{"Ms", "#0C2340", "maserati.com"});
        BRANDS.put("bentley", new String[]{"B", "#333333", "bentley.com"});
        BRANDS.put("peugeot", new String[]{"P", "#003B7C", "peugeot.com"});
        BRANDS.put("renault", new String[]{"R", "#FFCC00", "renault.com"});
        BRANDS.put("citroen", new String[]{"C", "#AC1E2D", "citroen.com"});
        BRANDS.put("dacia", new String[]{"D", "#003B7C", "dacia.com"});
        BRANDS.put("seat", new String[]{"S", "#B4000E", "seat.com"});
        BRANDS.put("skoda", new String[]{"Sk", "#4BA82E", "skoda.com"});
        BRANDS.put("mini", new String[]{"Mi", "#000000", "mini.com"});
        BRANDS.put("infiniti", new String[]{"I", "#1C1C1C", "infiniti.com"});
        BRANDS.put("acura", new String[]{"Ac", "#1C1C1C", "acura.com"});
        BRANDS.put("genesis", new String[]{"G", "#8B634B", "genesis.com"});
        BRANDS.put("cadillac", new String[]{"C", "#9E7B5C", "cadillac.com"});
        BRANDS.put("gmc", new String[]{"G", "#CC0000", "gmc.com"});
        BRANDS.put("lincoln", new String[]{"L", "#0C2340", "lincoln.com"});
        BRANDS.put("mitsubishi", new String[]{"Mi", "#E60012", "mitsubishi.com"});
        BRANDS.put("suzuki", new String[]{"Su", "#E4002B", "suzuki.com"});
        BRANDS.put("land rover", new String[]{"LR", "#005A2B", "landrover.com"});
        BRANDS.put("range rover", new String[]{"RR", "#005A2B", "landrover.com"});
    }
    
    /**
     * Get brand info for a vehicle name
     */
    private static String[] getBrandInfo(String vehicleName) {
        if (vehicleName == null || vehicleName.isEmpty()) return null;
        
        String lowerName = vehicleName.toLowerCase();
        for (Map.Entry<String, String[]> entry : BRANDS.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * Create a brand badge - tries logo first, falls back to text badge
     */
    public static Node createBrandBadge(String vehicleName) {
        String[] brandInfo = getBrandInfo(vehicleName);
        if (brandInfo == null) return null;
        
        String logoUrl = "https://logo.clearbit.com/" + brandInfo[2];
        
        // Check if this URL already failed
        if (failedUrls.containsKey(logoUrl)) {
            return createTextBadge(brandInfo);
        }
        
        // Check cache
        if (logoCache.containsKey(logoUrl)) {
            Image cached = logoCache.get(logoUrl);
            if (cached != null && !cached.isError()) {
                return createLogoView(cached);
            } else {
                return createTextBadge(brandInfo);
            }
        }
        
        // Try to load from network
        try {
            Image logo = new Image(logoUrl, 20, 20, true, true, false);
            
            if (logo.isError()) {
                failedUrls.put(logoUrl, true);
                return createTextBadge(brandInfo);
            }
            
            logoCache.put(logoUrl, logo);
            return createLogoView(logo);
        } catch (Exception e) {
            failedUrls.put(logoUrl, true);
            return createTextBadge(brandInfo);
        }
    }
    
    /**
     * Create an ImageView for a logo
     */
    private static ImageView createLogoView(Image logo) {
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(18);
        logoView.setFitHeight(18);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        return logoView;
    }
    
    /**
     * Create a text-based badge (colored circle with abbreviation)
     */
    private static StackPane createTextBadge(String[] brandInfo) {
        Circle circle = new Circle(10);
        try {
            circle.setFill(Color.web(brandInfo[1]));
        } catch (Exception e) {
            circle.setFill(Color.web("#6366f1"));
        }
        
        Label abbrev = new Label(brandInfo[0]);
        abbrev.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        StackPane badge = new StackPane(circle, abbrev);
        badge.setMinSize(20, 20);
        badge.setMaxSize(20, 20);
        
        return badge;
    }
    
    /**
     * Check if a vehicle name contains a known brand
     */
    public static boolean hasBrand(String vehicleName) {
        return getBrandInfo(vehicleName) != null;
    }
    
    /**
     * Detect brand name from vehicle name
     */
    public static String detectBrand(String vehicleName) {
        if (vehicleName == null || vehicleName.isEmpty()) return null;
        
        String lowerName = vehicleName.toLowerCase();
        for (String brand : BRANDS.keySet()) {
            if (lowerName.contains(brand)) {
                return brand.substring(0, 1).toUpperCase() + brand.substring(1);
            }
        }
        return null;
    }
}
