package com.izzy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskId implements Serializable {
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "scooter_id")
    private Long scooterId;

    // Constructors, getters, and setters, equals and hashCode

    public TaskId() {
    }

    public TaskId(Long orderId, Long scooterId) {
        this.orderId = orderId;
        this.scooterId = scooterId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getScooterId() {
        return scooterId;
    }

    public void setScooterId(Long scooterId) {
        this.scooterId = scooterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId that = (TaskId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(scooterId, that.scooterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, scooterId);
    }
}
