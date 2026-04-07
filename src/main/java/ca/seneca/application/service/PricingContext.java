package ca.seneca.application.service;

import ca.seneca.application.service.strategy.PricingStrategy;
import ca.seneca.application.service.strategy.StandardPricingStrategy;
import ca.seneca.application.service.strategy.WeekendPricingStrategy;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class PricingContext {
    public PricingStrategy chooseStrategy(LocalDate checkInDate) {
        DayOfWeek day = checkInDate.getDayOfWeek();

        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return new WeekendPricingStrategy();
        }
        return new StandardPricingStrategy();
    }
}
