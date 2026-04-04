package ca.seneca.application.model;

import ca.seneca.application.enums.WaitlistStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "WaitlistEntry")
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    private Integer waitlistId;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitlistStatus status;

    @ManyToOne
    @JoinColumn(name = "preferred_room_type_id")
    private RoomType preferredRoomType;

    public WaitlistEntry() {
    }

    public Integer getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(Integer waitlistId) {
        this.waitlistId = waitlistId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public WaitlistStatus getStatus() {
        return status;
    }

    public void setStatus(WaitlistStatus status) {
        this.status = status;
    }

    public RoomType getPreferredRoomType() {
        return preferredRoomType;
    }

    public void setPreferredRoomType(RoomType preferredRoomType) {
        this.preferredRoomType = preferredRoomType;
    }
}
