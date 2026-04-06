package com.hotel.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guests")
public class GuestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    @Column(nullable = false) private String phone;
    @Column private String email;
    @Column private String address;
    @Column private String city;
    @Column private String country;

    // Loyalty
    @Column(nullable = false) private boolean loyaltyMember = false;
    @Column private String loyaltyNumber;
    @Column(nullable = false) private int loyaltyPoints = 0;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationEntity> reservations = new ArrayList<>();

    public GuestEntity() {}

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }
    public boolean isLoyaltyMember() { return loyaltyMember; }
    public void setLoyaltyMember(boolean v) { this.loyaltyMember = v; }
    public String getLoyaltyNumber() { return loyaltyNumber; }
    public void setLoyaltyNumber(String v) { this.loyaltyNumber = v; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int v) { this.loyaltyPoints = v; }
    public void addLoyaltyPoints(int pts) { this.loyaltyPoints += pts; }
    public List<ReservationEntity> getReservations() { return reservations; }
}
