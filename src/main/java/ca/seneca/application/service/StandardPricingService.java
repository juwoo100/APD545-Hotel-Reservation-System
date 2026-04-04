package ca.seneca.application.service;

import ca.seneca.application.model.RoomType;
import ca.seneca.application.util.JpaUtil;
import ca.seneca.application.viewmodel.BookingDraft;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Map;

public class StandardPricingService implements PricingService {
    private final PricingStrategy pricingStrategy;

    public StandardPricingService() {
        this.pricingStrategy = new StandardPricingStrategy();
    }

    @Override
    public double calculateSubtotal(BookingDraft draft) {
        if (draft == null) {
            throw new IllegalArgumentException("Booking draft cannot be null.");
        }

        if (draft.getCheckInDate() == null || draft.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }

        if (!draft.getCheckOutDate().isAfter(draft.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        EntityManager em = JpaUtil.getEntityManager();

        try {
            double subtotal = 0.0;

            for (Map.Entry<String, Integer> entry : draft.getRoomSelections().entrySet()) {
                String roomTypeName = entry.getKey();
                int quantity = entry.getValue();

                RoomType roomType = em.createQuery(
                                "SELECT rt FROM RoomType rt WHERE LOWER(rt.typeName) = :typeName",
                                RoomType.class
                        )
                        .setParameter("typeName", roomTypeName.toLowerCase())
                        .getSingleResult();

                LocalDate current = draft.getCheckInDate();
                while (current.isBefore(draft.getCheckOutDate())) {
                    double nightlyRate = pricingStrategy.calculateNightlyRate(roomType.getBasePrice(), current);
                    subtotal += nightlyRate * quantity;
                    current = current.plusDays(1);
                }
            }

            return subtotal;

        } finally {
            em.close();
        }
    }

    @Override
    public double calculateTax(double subtotal) {
        return subtotal * 0.13;
    }

    @Override
    public double calculateTotal(BookingDraft draft) {
        double subtotal = calculateSubtotal(draft);
        return subtotal + calculateTax(subtotal);
    }
}
