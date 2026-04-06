package com.hotel.model;

import com.hotel.model.enums.PaymentMethod;
import com.hotel.model.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private GuestEntity guest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(nullable = false) private LocalDate checkInDate;
    @Column(nullable = false) private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    // Pricing
    @Column(nullable = false) private double roomSubtotal;
    @Column(nullable = false) private double addOnTotal;
    @Column(nullable = false) private double discountAmount = 0.0;
    @Column(nullable = false) private double tax;
    @Column(nullable = false) private double totalAmount;
    @Column(nullable = false) private double paidAmount = 0.0;

    // Add-ons as comma-separated string
    @Column private String addOns;

    @Enumerated(EnumType.STRING)
    @Column private PaymentMethod paymentMethod;

    @Column private String notes;
    @Column(nullable = false) private LocalDate createdDate = LocalDate.now();

    public ReservationEntity() {}

    public double getBalance() { return Math.max(0, totalAmount - paidAmount); }
    public long getNights() {
        return checkOutDate.toEpochDay() - checkInDate.toEpochDay();
    }

    // Getters/setters
    public Long getId() { return id; }
    public GuestEntity getGuest() { return guest; }
    public void setGuest(GuestEntity g) { this.guest = g; }
    public RoomEntity getRoom() { return room; }
    public void setRoom(RoomEntity r) { this.room = r; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate d) { this.checkInDate = d; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate d) { this.checkOutDate = d; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus s) { this.status = s; }
    public double getRoomSubtotal() { return roomSubtotal; }
    public void setRoomSubtotal(double v) { this.roomSubtotal = v; }
    public double getAddOnTotal() { return addOnTotal; }
    public void setAddOnTotal(double v) { this.addOnTotal = v; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double v) { this.discountAmount = v; }
    public double getTax() { return tax; }
    public void setTax(double v) { this.tax = v; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double v) { this.totalAmount = v; }
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double v) { this.paidAmount = v; }
    public String getAddOns() { return addOns; }
    public void setAddOns(String v) { this.addOns = v; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod v) { this.paymentMethod = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public LocalDate getCreatedDate() { return createdDate; }
}
