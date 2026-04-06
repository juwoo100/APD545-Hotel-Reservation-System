package com.hotel.model;

public class DoubleRoom extends Room {
    public DoubleRoom() {
        this.type = "Double";
        this.maxOccupancy = 4;
        this.basePrice = 200.0;
    }
}