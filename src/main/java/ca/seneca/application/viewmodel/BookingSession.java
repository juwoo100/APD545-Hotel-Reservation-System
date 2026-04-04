package ca.seneca.application.viewmodel;

/**
 * Static shared state across the kiosk booking flow.
 * One BookingDraft lives here for the duration of a booking.
 */
public class BookingSession {

    private static BookingDraft currentDraft = new BookingDraft();

    private BookingSession() {}

    public static BookingDraft getCurrentDraft() { return currentDraft; }

    /** Call this when starting a new booking to wipe all previous data. */
    public static void reset() {
        currentDraft = new BookingDraft();
    }

    /** Backward-compat alias for reset(). */
    public static void clear() { reset(); }
}
