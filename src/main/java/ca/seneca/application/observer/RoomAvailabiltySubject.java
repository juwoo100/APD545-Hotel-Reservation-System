package com.hotel.observer;

import com.hotel.model.RoomEntity;
import com.hotel.model.WaitlistEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Subject — maintains observer list and fires notifications.
 */
public class RoomAvailabilitySubject {

    private final List<RoomAvailabilityObserver> observers = new ArrayList<>();

    public void addObserver(RoomAvailabilityObserver o) { observers.add(o); }
    public void removeObserver(RoomAvailabilityObserver o) { observers.remove(o); }

    public void notifyRoomAvailable(RoomEntity room, List<WaitlistEntry> waitingGuests) {
        for (RoomAvailabilityObserver o : observers) {
            o.onRoomAvailable(room, waitingGuests);
        }
    }
}
