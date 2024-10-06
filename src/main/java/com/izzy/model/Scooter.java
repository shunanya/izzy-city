package com.izzy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "scooters")
public class Scooter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "battery_level", nullable = false)
    private Integer batteryLevel;
    @ManyToOne
    @JoinColumn(name = "zone", referencedColumnName = "id")
    private Zone zone;
    @Column(name = "speed_limit", nullable = false)
    private Integer speedLimit;
 /*   @JsonIgnore
    @OneToMany(mappedBy = "scooter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;
*/
    public Scooter() {
    }

    public Scooter(Long id, String identifier, String status, Integer batteryLevel, Integer speedLimit, Zone zone) {
        this.id = id;
        this.identifier = identifier;
        this.status = status;
        this.batteryLevel = batteryLevel;
        this.speedLimit = speedLimit;
        this.zone = zone;
    }

// getters and setters
/*
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
*/

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