package com.hotel.service.strategy;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Weekend pricing: applies a 20% surcharge on Friday and Saturday nights.
 */
public class WeekendPricingStrategy implements PricingStrategy {

    private static final double WEEKEND_MULTIPLIER = 1.20;

    @Override
    public double calculateNightlyTotal(double basePrice, int quantity,
                                        LocalDate checkIn, LocalDate checkOut) {
        double total = 0;
        LocalDate current = checkIn;
        while (current.isBefore(checkOut)) {
            double multiplier = isWeekend(current) ? WEEKEND_MULTIPLIER : 1.0;
            total += basePrice * quantity * multiplier;
            current = current.plusDays(1);
        }
        return total;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY;
    }
}
