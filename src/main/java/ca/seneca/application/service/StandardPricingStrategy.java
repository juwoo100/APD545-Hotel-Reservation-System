package ca.seneca.application.service;

import java.time.LocalDate;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculateNightlyRate(double basePrice, LocalDate date) {
        return basePrice;
    }
}
