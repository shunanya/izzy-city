package com.izzy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskDTO {
    private Long orderId;
    private Long scooterId;
    private Integer priority = 100;
    private String comment;
    private String status; // can be 'canceled' or 'completed'

    public TaskDTO() {
    }

    public TaskDTO(Task task){
        this.orderId = task.getOrderId();
        this.scooterId = task.getScooterId();
        this.priority = task.getPriority();
        this.comment = task.getComment();
        this.status = task.getStatus();
    }

    @JsonIgnore
    public boolean isValid(){
        return orderId != null && scooterId != null;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toJSONString() {
        try {
            return ((new ObjectMapper()).writeValueAsString(this));
        } catch (Exception ex) {
            return null; //TODO ???
        }
    }
}
