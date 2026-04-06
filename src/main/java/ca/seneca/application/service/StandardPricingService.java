package ca.seneca.application.service;

import ca.seneca.application.model.AddOn;
import ca.seneca.application.model.RoomType;
import ca.seneca.application.repository.AddOnRepository;
import ca.seneca.application.util.JpaUtil;
import ca.seneca.application.viewmodel.BookingDraft;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Map;

public class StandardPricingService implements PricingService {
    private final PricingStrategy pricingStrategy;
    private final AddOnRepository addOnRepository;

    public StandardPricingService() {
        this.pricingStrategy = new StandardPricingStrategy();
        this.addOnRepository = new AddOnRepository();
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
            System.out.println("roomSelections = " + draft.getRoomSelections());

            double subtotal = 0.0;
            // Calculation for room subtotal
            for (String addOnName : draft.getAddOns()) {
                AddOn addOn = addOnRepository.findByName(em, addOnName);
                if (addOn == null) {
                    throw new IllegalArgumentException("Add-on not found. [" + addOnName + "]");
                }

                double addOnPrice = addOn.getBasePrice();

                if (addOn.getPricingModel() != null && addOn.getPricingModel().equalsIgnoreCase("PER_NIGHT")) {
                    addOnPrice *= draft.getNights();
                }
                subtotal += addOnPrice;
            }

            for (Map.Entry<String, Integer> entry : draft.getRoomSelections().entrySet()) {
                String roomTypeName = entry.getKey();
                int quantity = entry.getValue();

                System.out.println("Looking for room type: [" + roomTypeName + "]");

                RoomType roomType = em.createQuery(
                                "SELECT rt FROM RoomType rt WHERE LOWER(rt.typeName) = :typeName",
                                RoomType.class
                        )
                        .setParameter("typeName", roomTypeName.toLowerCase())
                        .getResultStream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Room type not found: [" + roomTypeName + "]"));

                LocalDate current = draft.getCheckInDate();
                while (current.isBefore(draft.getCheckOutDate())) {
                    double nightlyRate = pricingStrategy.calculateNightlyRate(roomType.getBasePrice(), current);
                    subtotal += nightlyRate * quantity;
                    current = current.plusDays(1);
                }
            }

            System.out.println("Calculated subtotal = " + subtotal);
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
