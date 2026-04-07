package ca.seneca.application.service;

import ca.seneca.application.model.Reservation;
import ca.seneca.application.model.enums.AdminRole;

public class DiscountService {
    public double applyDiscount(double originalAmount, double discountPercent, AdminRole role) {
        validateDiscount(discountPercent, role);

        if (originalAmount < 0) {
            throw new IllegalArgumentException("Original amount cannot be negative.");
        }

        double discountAmount = originalAmount * (discountPercent / 100.0);
        return originalAmount - discountAmount;
    }

    public void applyDiscountToReservation(Reservation reservation, double discountPercent, AdminRole role) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation is required.");
        }
        if (reservation.getSubTotal() == null) {
            throw new IllegalArgumentException("Reservation subtotal is missing.");
        }

        validateDiscount(discountPercent, role);

        double subtotal = reservation.getSubTotal();
        double discountAmount = subtotal * (discountPercent / 100.0);
        double discountedSubtotal = subtotal - discountAmount;
        double tax = discountedSubtotal * 0.13;
        double totalAmount = discountedSubtotal + tax;

        reservation.setDiscountPercent(discountPercent);
        reservation.setDiscountAmount(discountAmount);
        reservation.setTotalAmount(totalAmount);
    }

    private void validateDiscount(double discountPercent, AdminRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Admin role is required.");
        }
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

    public double getMaxDiscountForRole(AdminRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Admin role is required.");
        }

        return switch (role) {
            case FRONT_DESK, ACCOUNTANT, SYSTEM_ADMIN -> 15.0;
            case MANAGER -> 30.0;
        };
    }
}
