package ca.seneca.application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ReservationRoom")
public class ReservationRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_room_id")
    private Integer reservationRoomId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(name = "nightly_rate")
    private Double nightlyRate;

    @Column(name = "line_total")
    private Double lineTotal;

    @Column(name = "guests_assigned")
    private Integer guestsAssigned;

    public ReservationRoom() {
    }

    public Integer getReservationRoomId() {
        return reservationRoomId;
    }

    public void setReservationRoomId(Integer reservationRoomId) {
        this.reservationRoomId = reservationRoomId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public Double getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(Double nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public Integer getGuestsAssigned() {
        return guestsAssigned;
    }

    public void setGuestsAssigned(Integer guestsAssigned) {
        this.guestsAssigned = guestsAssigned;
    }
}