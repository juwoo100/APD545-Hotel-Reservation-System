package com.hotel.observer;

import com.hotel.model.RoomEntity;
import com.hotel.model.WaitlistEntry;
import java.util.List;

/**
 * Observer Pattern — notified when a room becomes available.
 */
public interface RoomAvailabilityObserver {
    void onRoomAvailable(RoomEntity room, List<WaitlistEntry> waitingGuests);
}
