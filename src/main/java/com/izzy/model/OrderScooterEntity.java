package com.izzy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_scooter")
public class OrderScooterEntity {
    @EmbeddedId
    private OrderScooterId id;
    @ManyToOne
    @MapsId("order_id")
    @JoinColumn(name = "order_id")
    private OrderEntity order;
    @ManyToOne
    @MapsId("scooter_id")
    @JoinColumn(name = "scooter_id")
    private ScooterEntity scooter;
    @Basic
    @Column(name = "priority", nullable = false, columnDefinition = "integer default 1")
    private Integer priority;

    public OrderScooterEntity() {
    }

    // getters and setters

    public OrderScooterId getId() {
        return id;
    }

    public void setId(OrderScooterId id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public ScooterEntity getScooter() {
        return scooter;
    }

    public void setScooter(ScooterEntity scooter) {
        this.scooter = scooter;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}

