package ca.seneca.application.app;

import ca.seneca.application.model.AddOn;
import ca.seneca.application.model.Guest;
import ca.seneca.application.model.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingContext {
    private int adults;
    private int children;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private List<Room> selectedRooms = new ArrayList<>();
    private List<AddOn> selectedAddOns = new ArrayList<>();
    private Guest guest;
    private String specialRequest;

    public int getAdults() {
        return adults;
    }
    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }
    public void setChildren(int children) {
        this.children = children;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public List<Room> getSelectedRooms() {
        return selectedRooms;
    }
    public void setSelectedRooms(List<Room> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public List<AddOn> getSelectedAddOns() {
        return selectedAddOns;
    }
    public void setSelectedAddOns(List<AddOn> selectedAddOns) {
        this.selectedAddOns = selectedAddOns;
    }

    public Guest getGuest() {
        return guest;
    }
    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }
    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }
}
