package com.izzy.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")})
public class OrderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "action", nullable = false, length = 50)
    private String action;
    @Basic
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;
    @Basic
    @Column(name = "description", length = -1)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private UserEntity created_by;
    private Timestamp created_at;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    private UserEntity updated_by;
    @Basic
    @Column(name = "updated_at")
    private Timestamp updated_at;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", referencedColumnName = "id")
    private UserEntity assigned_to;
    @Basic
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taken_by", referencedColumnName = "id")
    private UserEntity taken_by;
    @Basic
    @Column(name = "taken_at")
    private Timestamp taken_at;
    @Basic
    @Column(name = "done_at")
    private Timestamp done_at;
    @ManyToMany
    @JoinTable(
            name = "order_scooter",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "scooter_id"))
    private Set<ScooterEntity> scooters;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderScooterEntity> orderScooters;

    // getters and setters

    public OrderEntity() {
    }

    public Set<OrderScooterEntity> getOrderScooters() {
        return orderScooters;
    }

    public void setOrderScooters(Set<OrderScooterEntity> orderScooters) {
        this.orderScooters = orderScooters;
    }

    public  Set<ScooterEntity> getScooters(){
        return scooters;
    }

    public void setScooters(Set<ScooterEntity> scooters) {
        this.scooters = scooters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getCreatedBy() {
        return created_by;
    }

    public void setCreatedBy(UserEntity created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }

    public UserEntity getUpdatedBy() {
        return updated_by;
    }

    public void setUpdatedBy(UserEntity updated_by) {
        this.updated_by = updated_by;
    }

    public Timestamp getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public UserEntity getAssignedTo() {
        return assigned_to;
    }

    public void setAssignedTo(UserEntity assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserEntity getTakenBy() {
        return taken_by;
    }

    public void setTakenBy(UserEntity taken_by) {
        this.taken_by = taken_by;
    }

    public Timestamp getTakenAt() {
        return taken_at;
    }

    public void setTakenAt(Timestamp taken_at) {
        this.taken_at = taken_at;
    }

    public Timestamp getDoneAt() {
        return done_at;
    }

    public void setDoneAt(Timestamp done_at) {
        this.done_at = done_at;
    }
}