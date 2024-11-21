package com.izzy.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Entity
@Table(name = "notifications", schema = "public")
@Check(constraints = "user_action IN ('approved', 'rejected')")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "created_at")
    private Timestamp createdAt = Timestamp.from(Instant.now());
    @Column(name = "user_action")
    private String userAction = null;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "scooter_id")
    private Long scooterId;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false),
            @JoinColumn(name = "scooter_id", referencedColumnName = "scooter_id", insertable = false, updatable = false)
    })
    private Task task = null;


    public Notification() {
    }

    public Notification(Long userId, Long orderId, Long scooterId) {
        this.userId = userId;
        this.orderId = orderId;
        this.scooterId = scooterId;
//        this.task = new Task(orderId, scooterId); // Initialize task

    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserAction() {
        return userAction;
    }

    public void setUserAction(String userAction) {
        this.userAction = userAction;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        try {
            return ((new ObjectMapper()).writeValueAsString(this));
        } catch (Exception e) {
            return super.toString(); //TODO ???
        }
    }

    public enum Action {
        REJECTED("rejected"), APPROVED("approved"), UNDEFINED("undefined");
        private final String value;

        Action(String value) {
            this.value = value;
        }

        public static Action getActionByValue(String value) {
            return Arrays.stream(values()).filter(m -> m.getValue().equals(value)).findFirst().orElse(UNDEFINED);
        }

        public static String stringActionByValue(String value) {
            return Arrays.stream(values()).filter(m -> m.getValue().equals(value)).findFirst().map(Action::getValue).orElse("UNDEFINED");
        }

        public String getValue() {
            return value;
        }
    }

}
