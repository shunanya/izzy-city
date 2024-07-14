package com.izzy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_scooter")
public class OrderScooter {
    @EmbeddedId
    private OrderScooterId id;
    @ManyToOne
    @MapsId("order_id")
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @MapsId("scooter_id")
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;
    @Basic
    @Column(name = "priority", nullable = false, columnDefinition = "integer default 1")
    private Integer priority;

    public OrderScooter() {
    }

    // getters and setters

    public OrderScooterId getId() {
        return id;
    }

    public void setId(OrderScooterId id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}

