package com.izzy.payload.request;

import com.izzy.model.misk.Task;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class OrderRequest {
    @Size(min = 1, max = 20)
    private String name;
    private String description;
    @Size(min = 3, max = 50)
    private String action;
    private Long createdBy;
    private Timestamp createdAt;
    private Long updatedBy;
    private Timestamp updatedAt;
    private Long assignedTo;
    @Size(min = 3, max = 50)
    private String status;
    private Long takenBy;
    private Timestamp takenAt;
    private Timestamp doneAt;
    private List<Task> tasks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTakenBy() {
        return takenBy;
    }

    public void setTakenBy(Long takenBy) {
        this.takenBy = takenBy;
    }

    public Timestamp getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Timestamp takenAt) {
        this.takenAt = takenAt;
    }

    public Timestamp getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(Timestamp doneAt) {
        this.doneAt = doneAt;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public enum Action {
        MOVE("Move"), CHARGE("Charge");
        private final String value;

        Action(String value) {
            this.value = value;
        }

        public static Boolean checkByValue(String value) {
            return Arrays.stream(values()).filter(month -> month.getValue().equals(value)).findFirst().map(v -> !v.toString().isBlank()).orElse(Boolean.FALSE);
        }

        public String getValue() {
            return value;
        }
    }

    public enum Status {
        CREATED("Created"), ASSIGNED("Assigned"), IN_PROGRESS("In_Progress"), COMPLETED("Completed"), CANCELED("Canceled");
        private final String value;

        Status(String value) {
            this.value = value;
        }

        public static Boolean checkByValue(String value) {
            return Arrays.stream(values()).filter(month -> month.getValue().equals(value)).findFirst().map(v -> !v.toString().isBlank()).orElse(Boolean.FALSE);
        }

        public String getValue() {
            return value;
        }
    }
}
