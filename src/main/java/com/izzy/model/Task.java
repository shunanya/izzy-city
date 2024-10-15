package com.izzy.model;

/*
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
*/

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    @JsonIgnore
    @EmbeddedId
    private TaskId id;

    /*   @JsonIgnore
       @ManyToOne(fetch = FetchType.LAZY)
       @MapsId("orderId")
       @JoinColumn(name = "order_id")
       private Order order;

       @JsonIgnore
       @ManyToOne(fetch = FetchType.LAZY)
       @MapsId("scooterId")
       @JoinColumn(name = "scooter_id")
       private Scooter scooter;
   */
    @Column(name = "priority", nullable = false, columnDefinition = "integer default 1")
    private Integer priority = 1;
    @Column(name = "comment")
    private @Nullable String comment;
/*

    @Type(JsonBinaryType.class)
    @Column(name = "info", columnDefinition = "jsonb")
    private JsonNode info;

*/
    @Transient
    private String status;

    public Task() {
    }

    // Constructors, getters, and setters

    public Task(TaskDTO taskDTO) {
        this(taskDTO.getOrderId(), taskDTO.getScooterId(), taskDTO.getPriority(), taskDTO.getComment());
    }

    public Task(@NonNull Long orderId, @NonNull Long scooterId) {
        this(orderId, scooterId, 1);
    }

    public Task(@NonNull Long orderId, @NonNull Long scooterId, Integer priority) {
        this(orderId, scooterId, priority, null);
    }

    public Task(@NonNull Long orderId, @NonNull Long scooterId, Integer priority, @Nullable String comment) {
        this.id = new TaskId(orderId, scooterId);
        this.priority = priority;
        this.comment = comment;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void updateStatus() { // Update task status
        this.status = getTaskStatusString();
    }

    public TaskId getId() {
        return id;
    }

    public void setId(TaskId id) {
        this.id = id;
    }

    public Long getOrderId() {
        return id.getOrderId();
    }

/*
    public void setOrderIdAndScooterId(Long orderId, Long scooterId) {
        if (id == null){
            id = new TaskId(orderId, scooterId);
        } else {
            id.setOrderId(orderId);
            id.setScooterId(scooterId);
        }
    }
*/

    public Long getScooterId() {
        return id.getScooterId();
    }

/*
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
*/

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public @Nullable String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        Status checkedStatus = Status.getStatusByString(status);
        if (checkedStatus != null) {
            this.status = checkedStatus.toString();
            this.priority = checkedStatus.value;
        }
    }

    @JsonIgnore
    public void setCanceled() {
        this.setStatus(Status.CANCELED.name());
    }

    @JsonIgnore
    public void setCompleted() {
        this.setStatus(Status.COMPLETED.name());
    }

    @JsonIgnore
    public void setActive(){
        this.setStatus(Status.ACTIVE.name());
    }

    @JsonIgnore
    public Status getTaskStatus() {
        return Status.getStatusByValue(this.priority);
    }

    @JsonIgnore
    public String getTaskStatusString() {
        String status = Status.getStatusByValue(this.priority).toString();
 /*       if (this.order != null && status.equalsIgnoreCase(Status.ACTIVE.toString())) {
            status = this.order.getStatus();
        }
*/
        return status;
    }

    @JsonIgnore
    public boolean isValid() {
        return this.id != null && this.id.getOrderId() != null && this.id.getScooterId() != null;
    }

    @JsonIgnore
    public boolean hasScooterId() {
        return this.id.getScooterId() != null;
    }

    @JsonIgnore
    public boolean hasOrderId(){
        return this.id.getOrderId() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return priority.equals(task.getPriority()) && Objects.equals(id, task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return '{' +
                "id: {orderId: " + id.getOrderId() + ", scooterId: " + id.getScooterId() + "}" +
                ", priority: " + priority +
                ", comment: '" + comment + '\'' +
                ", status: '" + status + '\'' +
                '}';
    }

    public enum Status {
        CANCELED(-1), COMPLETED(0), ACTIVE(1);
        private final int value;

        Status(int value) {
            this.value = value;
        }

        public static Status getStatusByString(String status) {
            try {
                return valueOf(status.toUpperCase());
            } catch (Exception ex) {
                return null;
            }
        }

        public static Status getStatusByValue(int value) {
            return Arrays.stream(values()).filter(m -> m.getValue() == value).findFirst().orElse(ACTIVE);
        }

        public static String getStatusStringByValue(int value) {
            return Arrays.stream(values()).filter(m -> m.getValue() == value).findFirst().map(Enum::toString).orElse("ACTIVE");
        }

        public int getValue() {
            return value;
        }
    }
}