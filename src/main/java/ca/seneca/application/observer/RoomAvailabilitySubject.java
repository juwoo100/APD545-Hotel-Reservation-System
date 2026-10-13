package ca.seneca.application.observer;

import ca.seneca.application.model.RoomEntity;
import ca.seneca.application.model.WaitlistEntry;
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
