package com.example.btcontroll;

import java.util.HashMap;
import java.util.Map;

public class DrinkPrices {
    public static final Map<String, Integer> PRICES = new HashMap<>();

//    Prices are in cents 1000 = $10
    static {
//        Mixed Drinks
        PRICES.put("whiscoke", 1);
        PRICES.put("whisga", 1000);
        PRICES.put("whislem", 1000);
        PRICES.put("marg", 1000);
        PRICES.put("teqoj", 1000);
        PRICES.put("teqspri", 1000);
        PRICES.put("teqlem", 1000);
        PRICES.put("teqsw", 1000);
        PRICES.put("vodcran", 1000);
        PRICES.put("screw", 1000);
        PRICES.put("vodsw", 1000);
        PRICES.put("vodspri", 1000);
        PRICES.put("vodlem", 1000);
        PRICES.put("rumcoke", 1000);
        PRICES.put("rumlem", 1000);
        PRICES.put("rumga", 1000);
//        Shots
        PRICES.put("shotwhis", 500);
        PRICES.put("shotteq", 500);
        PRICES.put("shotvod", 500);
        PRICES.put("shotrum", 500);
//        Splashes
        PRICES.put("splcran", 500);
        PRICES.put("splga", 500);
        PRICES.put("sploj", 500);
        PRICES.put("splsw", 500);
        PRICES.put("splspri", 500);
        PRICES.put("spllem", 500);
        PRICES.put("splcoke", 500);
    }
    public static int getPrice(String drinkName) {
        return PRICES.getOrDefault(drinkName, 0);
    }
    public static void setPrice(String drinkName, int price) {
        PRICES.put(drinkName, price);
    }
}
