package com.hotel.model;

public class SingleRoom extends Room {
    public SingleRoom() {
        this.type = "Single";
        this.maxOccupancy = 2;
        this.basePrice = 120.0;
    }
}