package com.hotel.factory;

import com.hotel.model.DeluxeRoom;
import com.hotel.model.DoubleRoom;
import com.hotel.model.PenthouseRoom;
import com.hotel.model.Room;
import com.hotel.model.SingleRoom;

/**
 * Factory Pattern — creates Room instances by type name.
 * Centralises default pricing and occupancy so callers never hard-code values.
 */
public class RoomFactory {

    private RoomFactory() {}

    /**
     * Creates a Room of the given type.
     * @param type "single", "double", "deluxe", or "penthouse" (case-insensitive)
     */
    public static Room createRoom(String type) {
        if (type == null) throw new IllegalArgumentException("Room type cannot be null");
        return switch (type.trim().toLowerCase()) {
            case "single"    -> new SingleRoom();
            case "double"    -> new DoubleRoom();
            case "deluxe"    -> new DeluxeRoom();
            case "penthouse" -> new PenthouseRoom();
            default -> throw new IllegalArgumentException("Unknown room type: '" + type + "'");
        };
    }

    /** Returns the display label shown in the kiosk UI. */
    public static String getDisplayName(String type) {
        return switch (type.trim().toLowerCase()) {
            case "single"    -> "Single Room — max 2 guests  |  $120/night";
            case "double"    -> "Double Room — max 4 guests  |  $200/night";
            case "deluxe"    -> "Deluxe Room — max 2 guests  |  $260/night";
            case "penthouse" -> "Penthouse — max 2 guests    |  $450/night";
            default          -> type;
        };
    }

    public static double getBasePrice(String type) {
        return createRoom(type).getBasePrice();
    }

    public static int getMaxOccupancy(String type) {
        return createRoom(type).getMaxOccupancy();
    }
}
