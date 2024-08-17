package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.model.OrderScooterId;
import com.izzy.model.Scooter;
import com.izzy.model.misk.Task;
import com.izzy.payload.response.TaskInfo;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.security.custom.service.CustomService;
import com.izzy.security.utils.Utils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final CustomService customService;
    private final OrderRepository orderRepository;
    private final ScooterRepository scooterRepository;
    private final OrderScooterRepository orderScooterRepository;

    public TaskService(CustomService customService,
                       OrderRepository orderRepository,
                       ScooterRepository scooterRepository,
                       OrderScooterRepository orderScooterRepository) {
        this.customService = customService;
        this.orderRepository = orderRepository;
        this.scooterRepository = scooterRepository;
        this.orderScooterRepository = orderScooterRepository;
    }

    /**
     * Appending a new task to the existing tasks
     *
     * @param orderId existing order id
     * @param task    a task to be appending
     * @return new list of tasks
     */
    @Transactional
    public List<Task> appendTask(@NonNull Long orderId, @NonNull Task task) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to append task to order created with user above your role");
        task.setOrderId(orderId);
        if (!task.isValid()) {
            throw new IllegalArgumentException("Task has not valid structure.");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order", "id", orderId));
        List<Task> tasks = order.getTasks();
        boolean updated = false;
        for (Task t : tasks) {
            if (t.equals(task)) {
                throw new BadRequestException("Given task is already included");
            } else if (t.getScooterId().equals(task.getScooterId())) {
                t.setPriority(task.getPriority());
                updated = true;
                break;
            }
        }
        if (!updated) tasks.add(task);
        return pushTasks(tasks);
    }

    public List<Task> markTaskAsCompleted(Long orderId, Task task){
       return markTask(orderId, task, Task.Status.COMPLETE);
    }

    public List<Task> markTaskAsCanceled(Long orderId, Task task) {
        return markTask(orderId, task, Task.Status.CANCEL);
    }

    private List<Task> markTask(@NonNull Long orderId, @NonNull Task task, Task.Status action) {
        task.setOrderId(orderId);
         if (!task.isValid()) {
            throw new IllegalArgumentException("Task has not valid structure.");
        }
        Order order = orderRepository.findById(task.getOrderId()).orElseThrow(()->new ResourceNotFoundException("Order", "id", orderId));
        List<Task> tasks = order.getTasks();
        boolean updated = false;
        Long id = task.getScooterId();
        for (Task t : tasks) {
            if (t.getScooterId().equals(id)) {
                switch (action) {
                    case COMPLETE -> t.setTaskAsCompleted();
                    case CANCEL -> t.setTaskAsCanceled();
                    default ->
                            throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", action));
                }
                updated = true;
                break;
            }
        }
        if (!updated)
            throw new ResourceNotFoundException("Task", "", String.format("{orderId: %s, ScooterId: %s}", orderId, task.getScooterId()));
        return pushTasks(tasks);
    }

    private List<Task> pushTasks(@NonNull List<Task> tasks) {
        List<Task> keptTasks = new ArrayList<>();
        if (!tasks.isEmpty()) {
//                orderScooterRepository.deleteByOrderId(orderId); // remove old tasks
//                orderScooterRepository.flush();
            keptTasks = Utils.rearrangeTasksPriorities(tasks);
            orderScooterRepository.saveAll(convertTasksToOrderScooters(keptTasks)); // store tasks
        }
        return keptTasks;
    }

    @Transactional
    public void removeTask(@NonNull Long orderId, @NonNull Long scooterId) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to remove task in order created with user above your role");
        Optional<OrderScooter> optionalOrderScooter = orderScooterRepository.findByOrderAndScooterIds(orderId, scooterId);
        optionalOrderScooter.ifPresentOrElse(
                os -> orderScooterRepository.deleteByOrderAndScooterIds(os.getOrder().getId(), os.getScooter().getId()),
                () -> {
                    throw new ResourceNotFoundException("Task", "", String.format("{orderId: %s, scooterId: %s}", orderId, scooterId));
                });
    }

    @Transactional
    public void removeTask(@NonNull Long orderId, @NonNull Task task) {
        removeTask(orderId, task.getScooterId());
    }

    private List<OrderScooter> getOrderScootersByOrderId(@NonNull Long orderId) {
        return orderScooterRepository.findByOrderId(orderId);
    }

    private OrderScooter convertTaskToOrderScooter(@NonNull Task task) {
        if (task.isValid()) {
            OrderScooter orderScooter = new OrderScooter();
            orderScooter.setId(new OrderScooterId(task.getOrderId(), task.getScooterId()));
            Order order = orderRepository.findById(task.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", task.getOrderId()));
            orderScooter.setOrder(order);
            Scooter scooter = scooterRepository.findById(task.getScooterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", task.getScooterId()));
            orderScooter.setScooter(scooter);
            orderScooter.setPriority(task.getPriority());
            orderScooter.setComment(task.getComment());
            return orderScooter;
        } else {
            throw new IllegalArgumentException("Task or Order has not valid structure.");
        }
    }

    private List<OrderScooter> convertTasksToOrderScooters(@NonNull List<Task> tasks) {
        List<OrderScooter> orderScooters = new ArrayList<>();
        for (Task task : tasks) {
            orderScooters.add(convertTaskToOrderScooter(task));
        }
        return orderScooters;
    }

    private Order getOrderByOrderId(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order", "id", orderId));
        if (!customService.checkAllowability(order))
            throw new AccessDeniedException("not allowed to get tasks from order created with user above your role");
        return order;
    }
    public List<Task> getTasksByOrderId(@NonNull Long orderId) {
         return getOrderByOrderId(orderId).getTasks();
    }

    public List<TaskInfo> getShortTaskInfosByOrderId(Long orderId){
        return TaskInfo.getShortTaskInfos(getOrderByOrderId(orderId));
    }

    public List<TaskInfo> getDetailedTaskInfosByOrderId(Long orderId){
        return TaskInfo.getDetailedTaskInfos(getOrderByOrderId(orderId));
    }

    private List<Order> getOrdersByAssignedToUserId(Long assignedToUserId){
        if (!customService.checkAllowability(assignedToUserId, true) ){
            throw new AccessDeniedException("not allowed to get order not assigned you");
        }
        return orderRepository.findOrdersByFilters(null, null, null, assignedToUserId);
    }

    public List<Task> getTasksByAssigned(Long assignedToUserId) {
        List<Order> orders = getOrdersByAssignedToUserId(assignedToUserId);

        List<Task> tasks = new ArrayList<>();
        if (!orders.isEmpty()) {
            orders.forEach(order->{
                List<Task> orderTasks = order.getTasks();
                if (!orderTasks.isEmpty()) {
                    tasks.addAll(orderTasks);
                }
            });
         }
        return Utils.rearrangeTasksPriorities(tasks);
    }

    private List<TaskInfo> getTaskInfosByAssigned(Long assignedToUserId, Boolean shortView){
        List<Order> orders = getOrdersByAssignedToUserId(assignedToUserId);

        List<TaskInfo> tasks = new ArrayList<>();
        if (!orders.isEmpty()) {
            orders.forEach(order->{
                List<TaskInfo> orderTaskInfos = shortView?TaskInfo.getShortTaskInfos(order):TaskInfo.getDetailedTaskInfos(order);
                if (!orderTaskInfos.isEmpty()) {
                    tasks.addAll(orderTaskInfos);
                }
            });
        }
        return tasks;

    }
    public List<TaskInfo> getShortTaskInfosByAssigned(Long assignedToUserId) {
        return getTaskInfosByAssigned(assignedToUserId, true);
    }

    public List<TaskInfo> getDetailedTaskInfosByAssigned(Long assignedToUserId) {
        return getTaskInfosByAssigned(assignedToUserId, false);
    }

    private List<Order> getOrdersAssignedMe(){
        Long userId = customService.currentUserId();
        return orderRepository.findOrdersByFilters(null, null, null, userId);
    }

    public List<Task> getTasksAssignedMe() {
        List<Order> orders = getOrdersAssignedMe();
        List<Task> tasks = new ArrayList<>();
        if (!orders.isEmpty()) {
            orders.forEach(order->{
                List<Task> orderTasks = order.getTasks();
                if (!orderTasks.isEmpty()) {
                    tasks.addAll(orderTasks);
                }
            });
        }
        return Utils.rearrangeTasksPriorities(tasks);
    }

    private List<TaskInfo> getTaskInfosAssignedMe(Boolean shortView){
        List<Order> orders = getOrdersAssignedMe();
        List<TaskInfo> taskInfos = new ArrayList<>();
        if (!orders.isEmpty()) {
            orders.forEach(order->{
                List<TaskInfo> orderTaskInfos = shortView?TaskInfo.getShortTaskInfos(order):TaskInfo.getDetailedTaskInfos(order);
                if (!orderTaskInfos.isEmpty()) {
                    taskInfos.addAll(orderTaskInfos);
                }
            });
        }
        return taskInfos;
    }
    public List<TaskInfo> getShortTaskInfosAssignedMe(){
        return getTaskInfosAssignedMe(true);
    }

    public List<TaskInfo> getDetailedTaskInfosAssignedMe(){
        return getTaskInfosAssignedMe(false);
    }

    public List<OrderScooter> rearrangeOrderScooterPriorities(Long orderId) {
        return Utils.rearrangeOrderScooterPriorities(getOrderScootersByOrderId(orderId));
    }

/*
    @Transactional
    public OrderScooter updatePriority(Long orderId, Long scooterId, Integer priority) {
        OrderScooter orderScooter = orderScooterRepository.findByOrderAndScooterIds(orderId, scooterId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderScooter", "Priority", priority));
        orderScooter.setPriority(priority);
        return orderScooterRepository.save(orderScooter);
    }

    public Integer getPriority(Long orderId, Long scooterId) {
        return orderScooterRepository.getPriorityByOrderIdAndScooterId(orderId, scooterId);
    }
*/

}
