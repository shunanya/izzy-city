package com.izzy.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.izzy.model.Order;
import com.izzy.model.Scooter;
import com.izzy.model.Task;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfo implements Serializable {
    private String orderName;
    private String orderDescription;
    private Long assignedTo;
    private String scooterNumber;
    private String type;
    private String status;
    private String comment;

    public TaskInfo(Order order, Scooter scooter, Task task, boolean shortInfo) {
        if (!shortInfo) {
            this.orderName = order.getName();
            this.orderDescription = order.getDescription();
            this.assignedTo = order.getAssignedTo();
        }
        this.type = order.getAction();
        this.scooterNumber = scooter.getIdentifier();
        this.status = task.getTaskStatusString();
        this.comment = task.getComment();
    }
/*
    public TaskInfo(Task task, boolean shortInfo){
        if (!shortInfo) {
            this.orderName = task.getOrder().getName();
            this.orderDescription = task.getOrder().getDescription();
            this.assignedTo = task.getOrder().getAssignedTo();
        }
        this.type = task.getOrder().getAction();
        this.scooterNumber = task.getScooter().getIdentifier();
        this.status = task.getStatus();
    }

    @JsonIgnore
    public static List<TaskInfo> getShortTaskInfos(List<Task> tasks){
        List<TaskInfo> taskInfos = new ArrayList<>();
        if (!tasks.isEmpty()) {
            tasks.sort(Comparator.comparingInt(Task::getPriority));
            tasks.forEach(task-> taskInfos.add(new TaskInfo(task, true)));
        }
        return taskInfos;
    }


    @JsonIgnore
    public static List<TaskInfo> getShortTaskInfos(Order order){
        return getShortTaskInfos(order.getTasks());
    }
*/

/*
    @JsonIgnore
    public static List<TaskInfo> getDetailedTaskInfos(List<Task> tasks){
        List<TaskInfo> taskInfos = new ArrayList<>();
        if (!tasks.isEmpty()) {
            tasks.sort(Comparator.comparingInt(Task::getPriority));
            tasks.forEach(task-> taskInfos.add(new TaskInfo(task, false)));
        }
        return taskInfos;
    }
*/

/*
    @JsonIgnore
    public static List<TaskInfo> getDetailedTaskInfos(Order order){
        return getDetailedTaskInfos(order.getTasks());
     }
*/

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getScooterNumber() {
        return scooterNumber;
    }

    public void setScooterNumber(String scooterNumber) {
        this.scooterNumber = scooterNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
