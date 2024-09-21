package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Order;
import com.izzy.model.Task;
import com.izzy.model.TaskDTO;
import com.izzy.model.User;
import com.izzy.payload.request.OrderRequest;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.TaskRepository;
import com.izzy.repository.UserRepository;
import com.izzy.security.custom.service.CustomService;
import com.izzy.security.utils.Utils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final TaskRepository taskRepository;
    private final CustomService customService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ScooterRepository scooterRepository,
                        TaskRepository taskRepository,
                        CustomService customService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.scooterRepository = scooterRepository;
        this.taskRepository = taskRepository;
        this.customService = customService;
    }

    /**
     * Returns complete list of orders
     *
     * @return List of orders
     */
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (!orders.isEmpty()) {
            List<Order> allowedOrders = new ArrayList<>();
            orders.forEach(order -> { // filtering permitted orders for current user
                if (customService.checkAllowability(order)) {
                    allowedOrders.add(order);
                }
            });
            return allowedOrders;
        }
        return orders;
    }

    /**
     * Converts the provided request data into Order entity
     *
     * @param orderRequest the provided data {@link OrderRequest}
     * @param orderId      Should be Null on creation.
     * @return entity Order on success
     * @throws ResourceNotFoundException if some resource cannot be found
     * @throws IllegalArgumentException  if some of provided arguments are wrong
     * @throws BadRequestException       if any required arguments are missing during creation
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    public Order getOrderFromOrderRequest(OrderRequest orderRequest, Long orderId) {
        boolean creation = (orderId == null);
        Order order = new Order();
        if (!creation) { //update
            order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        }
        String tmp = orderRequest.getName();
        if (tmp != null && !tmp.isBlank()) order.setName(tmp);
        tmp = orderRequest.getDescription();
        if (tmp != null && !tmp.isBlank()) order.setDescription(tmp);
        tmp = orderRequest.getAction();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Action.checkByValue(tmp)) order.setAction(tmp);
            else
                throw new IllegalArgumentException(String.format("Error: action field contains illegal value '%s'", tmp));
        } else if (creation) {
            throw new BadRequestException("Action must be defined while creating order");
        }
        if (creation) {
            order.setCreatedBy(customService.currentUserId());
            order.setCreatedAt(Timestamp.from(Instant.now()));
        } else {
            order.setUpdatedBy(customService.currentUserId());
            order.setUpdatedAt(Timestamp.from(Instant.now()));
        }
        Long id = orderRequest.getAssignedTo();
        if (id != null) {
            User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User.assignedTo", "id", id));
            if (customService.checkAllowability(existingUser)) {
                order.setAssignedTo(existingUser.getId());
                order.setStatus(OrderRequest.Status.ASSIGNED.toString());
            } else
                throw new AccessDeniedException("not allowed to assign order to user with above your role");
        }
        tmp = orderRequest.getStatus();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Status.checkByValue(tmp)) {
                order.setStatus(tmp);
            } else
                throw new IllegalArgumentException(String.format("status field contains illegal value '%s'", tmp));
        } else if (creation && order.getStatus() == null) {
            order.setStatus(OrderRequest.Status.CREATED.getValue());
        }
        Timestamp ts = orderRequest.getTakenAt();
        if (ts != null && !creation) order.setTakenAt(ts); // Only in the update request.
        ts = orderRequest.getDoneAt();
        if (ts != null && !creation) order.setDoneAt(ts); // Only in the update request.


        List<TaskDTO> tasksDTO = orderRequest.getTasks();
        if (tasksDTO != null && !tasksDTO.isEmpty()) {
            // Database generates orderId upon saving, keeping the order in virtual state until then.
            // Thus, tasks will be converted to Tasks later after saving.
            order.setRawTasks(Utils.rearrangeTasksDTOPriorities(tasksDTO));
        } else if (creation)
            throw new BadRequestException("Order must include at least one task.");

        return order;
    }

    /**
     * Completes the List of raw tasks {@link Task}
     *
     * @param rawTasks List of raw Task to be validated
     * @param order    existing Order that has to own of these tasks
     * @return the List of completed Tasks
     */
    public List<Task> completeTasks(@NonNull List<TaskDTO> rawTasks, @NonNull Order order) {
        List<Task> tasks = new ArrayList<>();
        if (order.isValid()) {
            for (TaskDTO taskDTO : rawTasks) {
                tasks.add(completeTask(taskDTO, order));
            }
        } else {
            throw new IllegalArgumentException("Task or Order has not valid structure.");
        }
        return tasks;
    }

    public Task completeTask(@NonNull TaskDTO rawTask, @NonNull Order order) {
        rawTask.setOrderId(order.getId());
        if (rawTask.isValid()) {
            return new Task(rawTask);
        } else {
            throw new IllegalArgumentException("Task or Order has not valid structure.");
        }
    }

    /**
     * Returns the filtered list of orders
     *
     * @param action     the filtering parameter
     * @param status     the filtering parameter
     * @param createdBy  the filtering parameter
     * @param assignedTo the filtering parameter
     * @return the filtered List of orders
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    public List<Order> getOrders(@Nullable String action,
                                 @Nullable String status,
                                 @Nullable Long createdBy,
                                 @Nullable Long assignedTo) {
        List<Order> orders;
        if (action != null || status != null || createdBy != null || assignedTo != null) {
            orders = orderRepository.findOrdersByFilters(action, status, createdBy, assignedTo);
        } else {
            orders = orderRepository.findAll();
        }
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<Order> allowedOrders = new ArrayList<>();
        orders.forEach(order -> { // filtering permitted orders for current user
            if (customService.checkAllowability(order)) {
                allowedOrders.add(order);
            }
        });
        return allowedOrders;
    }

    /**
     * Retrieves an order by their ID.
     *
     * @param id the ID of the order to retrieve.
     * @return the order
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        if (customService.checkAllowability(order)) {
            return order;
        } else {
            throw new AccessDeniedException("not allowed to get order created with user above your role");
        }
    }

    public Order getOrderByName(@NonNull String orderName) {
        return orderRepository.findOrderByName(orderName).orElseThrow(() -> new ResourceNotFoundException("Order", "name", orderName));
    }

    /**
     * Creates a new order.
     *
     * @param order the raw order with the required details to be saved.
     * @return saved in database order.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @Transactional
    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        List<TaskDTO> rawTasks = order.getRawTasks();
        if (rawTasks != null && !rawTasks.isEmpty()) { // new tasks are given
            //taskRepository.deleteByOrderId(savedOrder.getId()); // remove old tasks
            taskRepository.saveAll(completeTasks(rawTasks, savedOrder)); // add new tasks
        }
        return savedOrder;
    }

    /**
     * Updates an existing order.
     *
     * @param order the updated in memory order to be updated into database.
     * @return an updated order database ID.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @Transactional
    public Long updateOrder(@NonNull Order order) {
        if (!customService.checkAllowability(order))
            throw new AccessDeniedException("not allowed to update order created with user above your role");

        Order savedOrder = orderRepository.save(order);
        List<TaskDTO> rawTasks = order.getRawTasks();
        if (rawTasks != null && !rawTasks.isEmpty()) { // new tasks are given
            //taskRepository.deleteByOrderId(savedOrder.getId()); // remove old tasks
            taskRepository.saveAll(completeTasks(rawTasks, savedOrder)); // add new tasks
        }
        return savedOrder.getId();
    }

    /**
     * Deletes an order by their ID.
     *
     * @param orderId the ID of the order to delete.
     * @throws ResourceNotFoundException if the order is not found in database.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @Transactional
    public void deleteOrder(@NonNull Long orderId) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to delete order created with user above your role");
        orderRepository.deleteById(orderId);
    }
}
