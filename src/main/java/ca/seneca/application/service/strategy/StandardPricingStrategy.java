package ca.seneca.application.service.strategy;

import java.time.LocalDate;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculateNightlyRate(double basePrice, LocalDate date) {
        return basePrice;
    }
}
