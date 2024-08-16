package com.izzy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.izzy.model.misk.Task;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderScooter> orderScooters = new ArrayList<>();
    @Transient
    private List<Task> tasks = new ArrayList<>();

    public Order() {
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void loadTasks() { // Update tasks according to OrderScooters
        if (!orderScooters.isEmpty())
            this.tasks = orderScooters.stream().map(os -> new Task(os.getOrder().getId(), os.getScooter().getId(), os.getPriority(), os.getComment())).collect(Collectors.toList());
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

    public List<OrderScooter> getOrderScooters() {
        return orderScooters;
    }

    public void setOrderScooters(List<OrderScooter> orderScooters) {
        this.orderScooters = orderScooters;
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
}