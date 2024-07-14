package com.izzy.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "scooters")
public class ScooterEntity {
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "id")
    private ZoneEntity zone;
    @Basic
    @Column(name = "speed_limit", nullable = false)
    private Integer speedLimit;
    @ManyToMany(mappedBy = "scooters")
    private Set<OrderEntity> orders;
    @OneToMany(mappedBy = "scooter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderScooterEntity> orderScooters;

    public ScooterEntity() {
    }

    // getters and setters

    public Set<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderEntity> orders) {
        this.orders = orders;
    }

    public Set<OrderScooterEntity> getOrderScooters() {
        return orderScooters;
    }

    public void setOrderScooters(Set<OrderScooterEntity> orderScooters) {
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

    public ZoneEntity getZone() {
        return zone;
    }

    public void setZone(ZoneEntity zone) {
        this.zone = zone;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }
}