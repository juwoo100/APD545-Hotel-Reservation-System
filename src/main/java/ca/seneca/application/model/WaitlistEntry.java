package com.hotel.model;

import com.hotel.model.enums.RoomType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist")
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id", nullable = false)
    private GuestEntity guest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType requestedType;

    @Column(nullable = false) private LocalDate desiredCheckIn;
    @Column(nullable = false) private LocalDate desiredCheckOut;
    @Column(nullable = false) private LocalDateTime addedAt = LocalDateTime.now();
    @Column(nullable = false) private boolean notified = false;

    public WaitlistEntry() {}

    public Long getId() { return id; }
    public GuestEntity getGuest() { return guest; }
    public void setGuest(GuestEntity g) { this.guest = g; }
    public RoomType getRequestedType() { return requestedType; }
    public void setRequestedType(RoomType t) { this.requestedType = t; }
    public LocalDate getDesiredCheckIn() { return desiredCheckIn; }
    public void setDesiredCheckIn(LocalDate d) { this.desiredCheckIn = d; }
    public LocalDate getDesiredCheckOut() { return desiredCheckOut; }
    public void setDesiredCheckOut(LocalDate d) { this.desiredCheckOut = d; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public boolean isNotified() { return notified; }
    public void setNotified(boolean v) { this.notified = v; }
}
