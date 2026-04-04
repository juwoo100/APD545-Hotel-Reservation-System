package ca.seneca.application.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AddOn")
public class AddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "add_on_id")
    private Integer addOnId;

    @Column(name = "add_on_name", nullable = false)
    private String addOnName;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    private String description;

    @Column(name = "pricing_model")
    private String pricingModel;

    private String active;

    @OneToMany(mappedBy = "addOn")
    private List<ReservationAddOn> reservationAddOns = new ArrayList<>();

    public AddOn() {
    }

    public Integer getAddOnId() {
        return addOnId;
    }

    public void setAddOnId(Integer addOnId) {
        this.addOnId = addOnId;
    }

    public String getAddOnName() {
        return addOnName;
    }

    public void setAddOnName(String addOnName) {
        this.addOnName = addOnName;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<ReservationAddOn> getReservationAddOns() {
        return reservationAddOns;
    }

    public void setReservationAddOns(List<ReservationAddOn> reservationAddOns) {
        this.reservationAddOns = reservationAddOns;
    }
}