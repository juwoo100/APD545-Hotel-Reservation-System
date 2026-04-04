package ca.seneca.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekendPricingStrategy implements PricingStrategy {
    @Override
    public double calculateNightlyRate(double basePrice, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.FRIDAY || day == DayOfWeek.SUNDAY) {
            return basePrice * 1.20;
        }
        return basePrice;
    }
}
