package com.izzy.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "order_scooter")
public class OrderScooter {
    @EmbeddedId
    private OrderScooterId id;
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("scooterId")
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;
    @Column(name = "priority", nullable = false, columnDefinition = "integer default 1")
    private Integer priority;

    // Constructors, getters, and setters

    public OrderScooter() {
    }

    public OrderScooter(Order order, Scooter scooter) {
        this(order, scooter, 0);
    }

    public OrderScooter(Order order, Scooter scooter, Integer priority) {
        this.order = order;
        this.scooter = scooter;
        this.priority = priority;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderScooter that = (OrderScooter) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

