package com.izzy.model.misk;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Arrays;

public class Task implements Serializable {

    private Long scooterId;
    private int priority = 1;
    private @Nullable String comment;

    // Constructors, getters, and setters

    public Task() {
    }

    public Task(@NonNull Long scooterId, int priority) {
        this(scooterId, priority, null);
    }

    public Task(@NonNull Long scooterId, int priority, @Nullable String comment){
        this.scooterId = scooterId;
        this.priority = priority;
        this.comment = comment;
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

    public void setTaskAsCanceled () {this.priority = Status.CANCEL.value;}

    public void setTaskAsCompleted() {this.priority = Status.COMPLETE.value;}

    public boolean isValid() {
        return this.scooterId != null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "scooterId=" + scooterId +
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
            return Arrays.stream(values()).filter(m -> m.getValue()==value).findFirst().map(Enum::toString).orElse("ACTIVE");
        }

        public int getValue() {
            return value;
        }
    }
}