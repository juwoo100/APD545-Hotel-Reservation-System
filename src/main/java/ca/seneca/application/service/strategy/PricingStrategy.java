package com.hotel.service.strategy;

import java.time.LocalDate;

/**
 * Strategy Pattern: defines the algorithm interface for nightly room pricing.
 * Implementations can apply flat rates, weekend surcharges, seasonal rates, etc.
 */
public interface PricingStrategy {
    /**
     * Calculates the total room cost for all nights in the date range.
     * @param basePrice  nightly base price per room
     * @param quantity   number of rooms of this type
     * @param checkIn    first night
     * @param checkOut   departure date (not charged)
     */
    double calculateNightlyTotal(double basePrice, int quantity,
                                 LocalDate checkIn, LocalDate checkOut);
}
