package ca.seneca.application.service.strategy;

import java.time.LocalDate;

public interface PricingStrategy {
    double calculateNightlyRate(double basePrice, LocalDate date);
}
