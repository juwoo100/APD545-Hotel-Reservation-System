package com.hotel.viewmodel;

public class ReservationConfirmationHolder {
    private static String reservationNumber;

    private ReservationConfirmationHolder() {}

    public static String getReservationNumber() {
        return reservationNumber;
    }

    public static void setReservationNumber(String reservationNumber) {
        ReservationConfirmationHolder.reservationNumber = reservationNumber;
    }
}