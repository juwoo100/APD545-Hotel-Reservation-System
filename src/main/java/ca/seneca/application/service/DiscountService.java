package ca.seneca.application.service;

import ca.seneca.application.enums.AdminRole;

public class DiscountService {
    public double applyDiscount(double originalAmount, double discountPercent, AdminRole role) {
        validateDiscount(discountPercent, role);

        double discountAmount = originalAmount * (discountPercent / 100);
        return originalAmount - discountAmount;
    }
    private void validateDiscount(double discountPercent, AdminRole role) {
        if (discountPercent < 0) {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }
        if (discountPercent > 100) {
            throw new IllegalArgumentException("Discount cannot be greater than 100.");
        }

        double maxAllowed = switch (role) {
            case FRONT_DESK, ACCOUNTANT, SYSTEM_ADMIN -> 15.0;
            case MANAGER -> 30.0;
        };

        if (discountPercent > maxAllowed) {
            throw new IllegalArgumentException("Discount exceeds maximum limit for role: " + role);
        }
    }
}
