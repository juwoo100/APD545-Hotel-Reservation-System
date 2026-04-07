package ca.seneca.application.service.strategy;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekendPricingStrategy implements PricingStrategy {

    private static final double WEEKEND_MULTIPLIER = 1.20;

    @Override
    public double calculateNightlyRate(double basePrice, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY) {
            return basePrice * WEEKEND_MULTIPLIER;
        }
        return basePrice;
    }
}
