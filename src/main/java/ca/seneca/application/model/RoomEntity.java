package ca.seneca.application.model;

import ca.seneca.application.model.enums.RoomAvailabilityStatus;
import ca.seneca.application.model.enums.RoomType;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private int maxOccupancy;

    @Column(nullable = false)
    private double basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomAvailabilityStatus status = RoomAvailabilityStatus.AVAILABLE;

    public RoomEntity() {}
    public RoomEntity(String roomNumber, RoomType type, int maxOccupancy, double basePrice) {
        this.roomNumber = roomNumber; this.roomType = type;
        this.maxOccupancy = maxOccupancy; this.basePrice = basePrice;
    }

    public Long getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String n) { this.roomNumber = n; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType t) { this.roomType = t; }
    public int getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(int m) { this.maxOccupancy = m; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double p) { this.basePrice = p; }
    public RoomAvailabilityStatus getStatus() { return status; }
    public void setStatus(RoomAvailabilityStatus s) { this.status = s; }

    @Override
    public String toString() {
        return roomNumber + " (" + roomType + ") $" + basePrice + "/night";
    }
}
