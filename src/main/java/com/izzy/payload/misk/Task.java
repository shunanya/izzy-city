package com.izzy.payload.misk;

import java.io.Serializable;

public class Task implements Serializable {

    private Long scooterId;
    private int priority = 0;

    // Constructors, getters, and setters

    public Task() {
    }

    public Task(Long scooterId, int priority) {
        this.scooterId = scooterId;
        this.priority = priority;
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
}