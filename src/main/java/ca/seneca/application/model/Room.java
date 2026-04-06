package com.hotel.model;

public abstract class Room {
    protected String type;
    protected int maxOccupancy;
    protected double basePrice;

    public String getType() { return type; }
    public int getMaxOccupancy() { return maxOccupancy; }
    public double getBasePrice() { return basePrice; }
}