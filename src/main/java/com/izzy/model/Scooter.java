package com.izzy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "scooters")
public class Scooter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "identifier", nullable = false)
    private String identifier;
    @Basic
    @Column(name = "status", nullable = false)
    private String status;
    @Basic
    @Column(name = "battery_level", nullable = false)
    private Integer batteryLevel;
    @ManyToOne
    @JoinColumn(name = "zone", referencedColumnName = "id")
    private Zone zone;
    @Basic
    @Column(name = "speed_limit", nullable = false)
    private Integer speedLimit;
    @JsonIgnore
    @ManyToMany(mappedBy = "scooters")
    private Set<Order> orders;
    @JsonIgnore
    @OneToMany(mappedBy = "scooter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderScooter> orderScooters;

    public Scooter() {
    }

    // getters and setters

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<OrderScooter> getOrderScooters() {
        return orderScooters;
    }

    public void setOrderScooters(Set<OrderScooter> orderScooters) {
        this.orderScooters = orderScooters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }
}