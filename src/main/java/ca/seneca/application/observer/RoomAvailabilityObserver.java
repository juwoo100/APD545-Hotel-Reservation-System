package ca.seneca.application.observer;

import ca.seneca.application.model.RoomEntity;
import ca.seneca.application.model.WaitlistEntry;
import java.util.List;

/**
 * Observer Pattern — notified when a room becomes available.
 */
public interface RoomAvailabilityObserver {
    void onRoomAvailable(RoomEntity room, List<WaitlistEntry> waitingGuests);
}
