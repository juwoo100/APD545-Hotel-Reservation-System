package com.hotel.service.impl;

import com.hotel.service.BookingService;
import com.hotel.viewmodel.BookingDraft;

public class MockBookingService implements BookingService {
    @Override
    public String completeBooking(BookingDraft draft) {
        return "RES-" + System.currentTimeMillis();
    }
}