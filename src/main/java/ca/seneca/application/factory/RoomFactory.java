package ca.seneca.application.factory;

import ca.seneca.application.enums.RoomAvailabilityStatus;
import ca.seneca.application.model.Room;
import ca.seneca.application.model.RoomType;

public class RoomFactory {

    public static Room createRoom(String roomNumber, int floor, RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("RoomType cannot be null");
        }
        if (roomNumber == null) {
            throw new IllegalArgumentException("RoomNumber cannot be null");
        }

        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomNumber(roomNumber);
        room.setFloor(floor);
        room.setAvailabilityStatus(RoomAvailabilityStatus.AVAILABLE);
        return room;
    }
    public static int getMaxOccupancy(String type) {
        return switch (type.trim().toLowerCase()) {
            case "single" -> 2;
            case "double" -> 4;
            case "deluxe" -> 2;
            case "penthouse" -> 2;
            default -> throw new IllegalArgumentException("Unknown room type: " + type);
        };
    }

    public static String getDisplayName(String type) {
        return switch (type.trim().toLowerCase()) {
            case "single" -> "Single Room — max 2 guests";
            case "double" -> "Double Room — max 4 guests";
            case "deluxe" -> "Deluxe Room — max 2 guests";
            case "penthouse" -> "Penthouse — max 2 guests";
            default -> type;
        };
    }
}
