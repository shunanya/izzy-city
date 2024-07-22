package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.*;
import com.izzy.payload.request.OrderRequest;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final OrderScooterRepository orderScooterRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ScooterRepository scooterRepository,
                        OrderScooterRepository orderScooterRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.scooterRepository = scooterRepository;
        this.orderScooterRepository = orderScooterRepository;
    }

    /**
     * Returns complete list of orders
     *
     * @return List of orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Converts the provided request data into Order entity
     *
     * @param orderRequest the provided data {@link OrderRequest}
     * @param orderId  Should be Null on creation a new order.
     * @return entity Order on success
     */
    public Order getOrderFromOrderRequest(OrderRequest orderRequest, Long orderId) {
        Order order = new Order();

        String tmp = orderRequest.getName();
        if (tmp != null && !tmp.isBlank()) order.setName(tmp);
        tmp = orderRequest.getDescription();
        if (tmp != null && !tmp.isBlank()) order.setDescription(tmp);
        tmp = orderRequest.getAction();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Action.checkByValue(tmp)) order.setAction(tmp);
            else
                throw new IllegalArgumentException(String.format("Error: action field contains illegal value '%s'", tmp));
        }
        Long id = orderRequest.getCreatedBy();
        if (id != null) {
            Optional<User> existingUser = userRepository.findById(id);
            existingUser.ifPresent(order::setCreatedBy);
        }
        Timestamp ts = orderRequest.getCreatedAt();
        if (orderId == null) {
            order.setCreatedAt(Timestamp.from(Instant.now()));
        } else if (ts != null) {
            order.setCreatedAt(ts);
        }
        id = orderRequest.getAssignedTo();
        if (id != null) {
            Optional<User> existingUser = userRepository.findById(id);
            existingUser.ifPresent(order::setCreatedBy);
        }
        tmp = orderRequest.getStatus();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Status.checkByValue(tmp)) order.setStatus(tmp);
            else
                throw new IllegalArgumentException(String.format("Error: status field contains illegal value '%s'", tmp));
        }
        id = orderRequest.getTakenBy();
        if (id != null && orderId != null) { // Only in the update request.
            Optional<User> existingUser = userRepository.findById(id);
            existingUser.ifPresent(order::setTakenBy);
        }
        ts = orderRequest.getTakenAt();
        if (ts != null && orderId != null) order.setTakenAt(ts); // Only in the update request.
        ts = orderRequest.getDoneAt();
        if (ts != null && orderId != null) order.setDoneAt(ts); // Only in the update request.
        order.setScooters(new HashSet<>());
        order.setOrderScooters(new HashSet<>());
        Long[] scootersId = orderRequest.getScooters();
        if (scootersId != null && scootersId.length > 0) {
            Set<Scooter> scooters = new HashSet<>();
            Set<OrderScooter> orderScooters = new HashSet<>();
            for (Long s : scootersId) {
                Optional<Scooter> existingScooter = scooterRepository.findById(s);
                existingScooter.ifPresent(scooters::add);
             }
            if (orderId == null) { // create
                order.setOrderScooters(orderScooters); // put empty set while create request
            }
            order.setScooters(scooters);
            if (scooters.isEmpty()) {
                throw new BadRequestException("Error: Scooters are not being recognized.");
            }
        }
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
     */
    public List<Order> getOrders(String action, String status, Long createdBy, Long assignedTo) {
        if (action != null || status != null || createdBy != null || assignedTo != null) {
            return orderRepository.findOrdersByFilters(action, status, createdBy, assignedTo);
        } else {
            return orderRepository.findAll();
        }
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        return orderRepository.findById(id).map(existingOrder -> {
            String tmp = order.getAction();
            if (tmp != null && !tmp.isBlank()) existingOrder.setAction(tmp);
            tmp = order.getName();
            if (tmp != null && !tmp.isBlank()) existingOrder.setName(tmp);
            tmp = order.getDescription();
            if (tmp != null && !tmp.isBlank()) existingOrder.setDescription(tmp);
            User u = order.getCreatedBy();
            if (u != null) existingOrder.setCreatedBy(u);
            Timestamp ts = order.getCreatedAt();
            if (ts != null) existingOrder.setCreatedAt(ts);
            u = order.getUpdatedBy();
            if (u != null) existingOrder.setUpdatedBy(u);
            ts = order.getUpdatedAt();
            if (ts != null) existingOrder.setUpdatedAt(ts);
            u = order.getAssignedTo();
            if (u != null) existingOrder.setAssignedTo(u);
            tmp = order.getStatus();
            if (tmp != null && !tmp.isBlank()) existingOrder.setStatus(tmp);
            u = order.getTakenBy();
            if (u != null) existingOrder.setTakenBy(u);
            ts = order.getTakenAt();
            if (ts != null) existingOrder.setTakenAt(ts);
            ts = order.getDoneAt();
            if (ts != null) existingOrder.setDoneAt(ts);
            Set<Scooter> sc = order.getScooters();
            if (sc != null && !sc.isEmpty()) {
                Set<Scooter> esc = existingOrder.getScooters();
                if (esc != null && !esc.isEmpty()) { // remove existing links (OrderScooter) between order and scooters
                    esc.forEach(s -> {
                        orderScooterRepository.findByOrderIdScooterId(id, s.getId())
                                .ifPresent(os -> orderScooterRepository.deleteById(os.getId()));
                    });
                    esc.clear();
                    // Create new OrderScooter links based on request
                    Set<OrderScooter> orderScooters = new HashSet<>();
                    sc.forEach(s -> {
                        orderScooters.add(new OrderScooter(existingOrder, s));
                    });
                    existingOrder.setOrderScooters(orderScooters);
                }
                existingOrder.setScooters(sc); // put scooters as well
            }
            return orderRepository.save(existingOrder);
        }).orElse(null);
    }

    public boolean deleteOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            return true;
        }).orElse(false);
    }

    public Set<Scooter> getScootersByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "Scooter", orderId));
        return order.getScooters();
    }

    public List<OrderScooter> getOrderScootersByOrderId(Long orderId) {
        return orderScooterRepository.findByOrderId(orderId);
    }

    public OrderScooter updatePriority(Long orderId, Long scooterId, Integer priority) {
        OrderScooter orderScooter = orderScooterRepository.findByOrderIdScooterId(orderId, scooterId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderScooter", "Priority", priority));
        orderScooter.setPriority(priority);
        return orderScooterRepository.save(orderScooter);
    }

    public Integer getPriority(Long orderId, Long scooterId) {
        return orderScooterRepository.getPriorityByOrderIdAndScooterId(orderId, scooterId);
    }

}
