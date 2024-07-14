package com.izzy.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderScooterId implements Serializable {
    private Long order_id;
    private Long scooter_id;

    public OrderScooterId() {}

    public OrderScooterId(Long order_id, Long scooter_id) {
        this.order_id = order_id;
        this.scooter_id = scooter_id;
    }

    // getters and setters, equals, and hashCode

    public Long getOrderId() {
        return order_id;
    }

    public void setOrderId(Long orderId) {
        this.order_id = orderId;
    }

    public Long getScooterId() {
        return scooter_id;
    }

    public void setScooterId(Long scooterId) {
        this.scooter_id = scooterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderScooterId that = (OrderScooterId) o;
        return Objects.equals(order_id, that.order_id) &&
                Objects.equals(scooter_id, that.scooter_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order_id, scooter_id);
    }
}
