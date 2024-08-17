package com.izzy.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.model.misk.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfo implements Serializable {
    private String orderName;
    private String orderDescription;
    private Long assignedTo;
    private String scooterNumber;
    private String type;
    private String status;

    public TaskInfo(String scooterNumber, String type, String status){
        this.scooterNumber = scooterNumber;
        this.type = type;
        this.status = status;
    }

    public TaskInfo(String orderName, String orderDescription, Long assignedTo, String scooterNumber, String type, String status){
        this.orderName = orderName;
        this.orderDescription = orderDescription;
        this.assignedTo = assignedTo;
        this.scooterNumber = scooterNumber;
        this.type = type;
        this.status = status;
    }

    @JsonIgnore
    public static List<TaskInfo> getShortTaskInfos(Order order){
        List<TaskInfo> taskInfos = new ArrayList<>();
        List<OrderScooter> orderScooters = order.getOrderScooters();
        if (!orderScooters.isEmpty()) {
            orderScooters.sort(Comparator.comparingInt(OrderScooter::getPriority));
            orderScooters.forEach(os-> {
                Integer priority = os.getPriority();
                String status = priority.equals(Task.Status.CANCELED.getValue())? Task.Status.CANCELED.toString()
                        : priority.equals(Task.Status.COMPLETED.getValue())? Task.Status.COMPLETED.toString()
                        : order.getStatus();
                taskInfos.add(new TaskInfo(os.getScooter().getIdentifier(), order.getAction(), status));
            });
        }
        return taskInfos;
    }

    @JsonIgnore
    public static List<TaskInfo> getDetailedTaskInfos(Order order){
        List<TaskInfo> taskInfos = new ArrayList<>();
        List<OrderScooter> orderScooters = order.getOrderScooters();
        if (!orderScooters.isEmpty()) {
            orderScooters.sort(Comparator.comparingInt(OrderScooter::getPriority));
            orderScooters.forEach(os-> {
                Integer priority = os.getPriority();
                String status = priority.equals(Task.Status.CANCELED.getValue())? Task.Status.CANCELED.toString()
                        : priority.equals(Task.Status.COMPLETED.getValue())? Task.Status.COMPLETED.toString()
                        : order.getStatus();
                taskInfos.add(new TaskInfo(order.getName(), order.getDescription(), order.getAssignedTo(), os.getScooter().getIdentifier(), order.getAction(), status));
            });
        }
        return taskInfos;
    }

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
}
