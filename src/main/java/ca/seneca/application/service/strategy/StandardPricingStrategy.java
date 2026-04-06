package com.hotel.service.strategy;

import java.time.LocalDate;

/**
 * Standard pricing: flat base price every night — no surcharges.
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculateNightlyTotal(double basePrice, int quantity,
                                        LocalDate checkIn, LocalDate checkOut) {
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        return basePrice * quantity * nights;
    }
}
