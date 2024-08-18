package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.*;
import com.izzy.model.misk.Task;
import com.izzy.payload.request.OrderRequest;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import com.izzy.repository.ScooterRepository;
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
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final OrderScooterRepository orderScooterRepository;
    private final CustomService customService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ScooterRepository scooterRepository,
                        OrderScooterRepository orderScooterRepository,
                        CustomService customService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.scooterRepository = scooterRepository;
        this.orderScooterRepository = orderScooterRepository;
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
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isPresent()) order = orderOptional.get();
            else throw new ResourceNotFoundException("Order", "id", orderId);
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
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                if (customService.checkAllowability(existingUser.get())) {
                    order.setAssignedTo(existingUser.get().getId());
                } else throw new AccessDeniedException("not allowed to assign order to user with above your role");
            }
        }
        tmp = orderRequest.getStatus();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Status.checkByValue(tmp)) order.setStatus(tmp);
            else
                throw new IllegalArgumentException(String.format("status field contains illegal value '%s'", tmp));
        } else if (creation) order.setStatus(OrderRequest.Status.CREATED.getValue());
        Timestamp ts = orderRequest.getTakenAt();
        if (ts != null && !creation) order.setTakenAt(ts); // Only in the update request.
        ts = orderRequest.getDoneAt();
        if (ts != null && !creation) order.setDoneAt(ts); // Only in the update request.


        List<Task> tasks = orderRequest.getTasks();
        if (tasks != null && !tasks.isEmpty()) {
            tasks = Utils.rearrangeTasksPriorities(tasks);
            order.setTasks(tasks);
            // Database generates orderId upon saving, keeping the order in virtual state until then.
            // Thus, tasks will be converted to OrderScooters later after saving.
            if (!creation) // Only in the update request.
                order.setOrderScooters(convertTasksToOrderScooters(tasks, order));
        } else if (creation)
            throw new BadRequestException("Order must include at least one task.");

        return order;
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
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            if (customService.checkAllowability(orderOptional.get())) {
                return orderOptional.get();
            } else {
                throw new AccessDeniedException("not allowed to get order created with user above your role");
            }
        } else {
            throw new ResourceNotFoundException("Order", "id", id);
        }
    }

    /**
     * Converts List of Task {@link Task} to List of OrderScooter {@link OrderScooter}
     *
     * @param tasks List of Task to be converted
     * @param order existing Order that has to own of these tasks
     * @return the List of OrderScooter
     */
    public List<OrderScooter> convertTasksToOrderScooters(@NonNull List<Task> tasks, @NonNull Order order) {
        List<OrderScooter> orderScooters = new ArrayList<>();
        for (Task task : tasks) {
            orderScooters.add(convertTaskToOrderScooter(task, order));
        }
        return orderScooters;
    }

    private OrderScooter convertTaskToOrderScooter(@NonNull Task task, @NonNull Order order) {
        task.setOrderId(order.getId());
        if (task.isValid() && order.isValid()) {
            OrderScooter orderScooter = new OrderScooter();
            orderScooter.setId(new OrderScooterId(task.getOrderId(), task.getScooterId()));
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
        List<Task> tasks = order.getTasks();
        if (tasks != null && !tasks.isEmpty()) { // new tasks are given
            //orderScooterRepository.deleteByOrderId(savedOrder.getId()); // remove old tasks
            orderScooterRepository.saveAll(convertTasksToOrderScooters(tasks, savedOrder)); // add new tasks
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
        return savedOrder.getId();
    }

    /**
     * Deletes an order by their ID.
     *
     * @param orderId the ID of the order to delete.
     * @return nothing
     * @throws ResourceNotFoundException if the order is not found in database.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    public void deleteOrder(@NonNull Long orderId) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to delete order created with user above your role");
        orderRepository.deleteById(orderId);
    }
}
