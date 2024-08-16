package com.izzy.model.misk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Task implements Serializable {

//    @JsonIgnore
    private Long orderId;
    private Long scooterId;
//    @JsonIgnore
    private int priority = 1;
    private @Nullable String comment;

    // Constructors, getters, and setters

    public Task() {
    }

    public Task(Long orderId, @NonNull Long scooterId, int priority) {
        this(orderId, scooterId, priority, null);
    }

    public Task(Long orderId, @NonNull Long scooterId, int priority, @Nullable String comment) {
        this.orderId = orderId;
        this.scooterId = scooterId;
        this.priority = priority;
        this.comment = comment;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    public void setTaskAsCanceled() {
        this.priority = Status.CANCEL.value;
        this.comment = Status.CANCEL.toString();
    }

    public void setTaskAsCompleted() {
        this.priority = Status.COMPLETE.value;
        this.comment = Status.COMPLETE.toString();
    }

    @JsonIgnore
    public boolean isValid() {
        return this.orderId != null && this.scooterId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getPriority() == task.getPriority() && Objects.equals(getScooterId(), task.getScooterId()) && Objects.equals(getOrderId(), task.getOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScooterId(), getPriority());
    }

    @Override
    public String toString() {
        return "Task{" +
                "orderId=" + orderId +
                ", scooterId=" + scooterId +
                ", priority=" + priority +
                ", comment='" + comment + '\'' +
                '}';
    }

    public enum Status {
        CANCEL(-1), COMPLETE(0);
        private final int value;

        Status(int value) {
            this.value = value;
        }

        public static String statusByValue(int value) {
            return Arrays.stream(values()).filter(m -> m.getValue() == value).findFirst().map(Enum::toString).orElse("ACTIVE");
        }

        public int getValue() {
            return value;
        }
    }
}