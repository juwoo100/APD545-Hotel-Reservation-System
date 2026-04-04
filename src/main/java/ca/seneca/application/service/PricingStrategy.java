package ca.seneca.application.service;

import java.time.LocalDate;

public interface PricingStrategy {
    double calculateNightlyRate(double basePrice, LocalDate date);
}
