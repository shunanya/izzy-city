package com.izzy.model;

import com.izzy.model.misk.Task;
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
    private Integer priority = 1;
    @Column(name="comment")
    private String comment;

    // Constructors, getters, and setters

    public OrderScooter() {
    }
    public OrderScooter(Order order, Scooter scooter) {this(order, scooter, 1);}
    public OrderScooter(Order order, Scooter scooter, Integer priority) {this(order, scooter, priority, null);}
    public OrderScooter(Order order, Scooter scooter, Integer priority, String comment) {
        this.order = order;
        this.scooter = scooter;
        this.priority = priority;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTaskAsCanceled() {
        this.priority = Task.Status.CANCELED.getValue();
        this.comment = Task.Status.CANCELED.toString();
    }

    public void setTaskAsCompleted() {
        this.priority = Task.Status.COMPLETED.getValue();
        this.comment = Task.Status.COMPLETED.toString();
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

