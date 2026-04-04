package ca.seneca.application.service;

import ca.seneca.application.viewmodel.BookingDraft;

public interface PricingService {
    double calculateSubtotal(BookingDraft draft);
    double calculateTax(double subtotal);
    double calculateTotal(BookingDraft draft);
}