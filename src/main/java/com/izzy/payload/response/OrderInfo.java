package com.izzy.payload.response;

import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.model.misk.Task;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class OrderInfo implements Serializable {
    private Long id;
    private String action;
    private String name;
    private String description;
    private Long createdBy;
    private Timestamp createdAt;
    private Long updatedBy;
    private Timestamp updatedAt;
    private Long assignedTo;
    private String status;
    private Long takenBy;
    private Timestamp takenAt;
    private Timestamp doneAt;
    private List<Task> tasks;

    public OrderInfo() {
    }

    public OrderInfo(Order order) {
        this.id = order.getId();
        this.action = order.getAction();
        this.name = order.getName();
        this.description = order.getDescription();
        this.createdBy = order.getCreatedBy();
        this.createdAt = order.getCreatedAt();
        this.updatedBy = order.getUpdatedBy();
        this.updatedAt = order.getUpdatedAt();
        this.assignedTo = order.getAssignedTo();
        this.status = order.getStatus();
        this.takenAt = order.getTakenAt();
        this.doneAt = order.getDoneAt();
        List<OrderScooter> orderScooter = order.getOrderScooters();
        if (orderScooter != null && !orderScooter.isEmpty()) {
            this.tasks = orderScooter.stream().map(os -> new Task(os.getScooter().getId(), os.getPriority())).collect(Collectors.toList());
        }
    }
// Getters and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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
}
