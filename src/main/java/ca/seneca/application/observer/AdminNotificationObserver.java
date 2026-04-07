package ca.seneca.application.observer;

import ca.seneca.application.logging.AppLogger;
import ca.seneca.application.model.RoomEntity;
import ca.seneca.application.model.WaitlistEntry;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.logging.Logger;

/**
 * Concrete observer: shows an admin alert and logs when a room becomes available.
 */
public class AdminNotificationObserver implements RoomAvailabilityObserver {

    private static final Logger log = AppLogger.get(AdminNotificationObserver.class);

    @Override
    public void onRoomAvailable(RoomEntity room, List<WaitlistEntry> waitingGuests) {
        String msg = "Room " + room.getRoomNumber() + " (" + room.getRoomType() + ") is now AVAILABLE.";
        if (!waitingGuests.isEmpty()) {
            msg += "\n" + waitingGuests.size() + " guest(s) are on the waitlist for this type.";
            msg += "\nFirst in queue: " + waitingGuests.get(0).getGuest().getFullName();
        }

        log.info("[OBSERVER] " + msg);

        final String finalMsg = msg;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Room Available — Waitlist Notification");
            alert.setHeaderText("🔔 Room Now Available");
            alert.setContentText(finalMsg);
            alert.show();
        });
    }
}
