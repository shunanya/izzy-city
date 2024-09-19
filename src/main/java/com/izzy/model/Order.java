package com.izzy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@JsonPropertyOrder({
        "id",
        "name",
        "description",
        "action",
        "status",
        "assigned_to",
        "created_by",
        "created_at",
        "updated_by",
        "updated_at",
        "taken_at",
        "done_at",
        "tasks"
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name = "";
    @Column(name = "description", length = -1)
    private String description;
    @Column(name = "action", nullable = false, length = 50)
    private String action = "";
    @Column(name = "created_by")
    private Long createdBy; // default value = 0
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp createdAt;
    @Column(name = "updated_by")
    private Long updatedBy; // default value = 0
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp updatedAt;
    @Column(name = "assigned_to")
    private Long assignedTo; // default value = 0
    @Column(name = "status", nullable = false, length = 50)
    private String status = "";
    @Column(name = "taken_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp takenAt;
    @Column(name = "done_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp doneAt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private List<Task> tasks = new ArrayList<>();

    @JsonIgnore
    private transient List<TaskDTO> rawTasks = new ArrayList<>();

    public Order() {
    }

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

    @JsonIgnore
    public boolean isValid() {
        return (this.id != null && action != null && name != null && status != null);
    }

    public List<TaskDTO> getRawTasks() {
        return rawTasks;
    }

    public void setRawTasks(List<TaskDTO> rawTasks) {
        this.rawTasks = rawTasks;
    }
}