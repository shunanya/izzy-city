package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.model.OrderScooterId;
import com.izzy.model.Scooter;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderScooterRepository orderScooterRepository;

    public OrderService(OrderRepository orderRepository, OrderScooterRepository orderScooterRepository) {
        this.orderRepository = orderRepository;
        this.orderScooterRepository = orderScooterRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

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

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        return orderRepository.findById(id).map(existingOrder -> {
            existingOrder.setAction(order.getAction());
            existingOrder.setName(order.getName());
            existingOrder.setDescription(order.getDescription());
            existingOrder.setCreatedBy(order.getCreatedBy());
            existingOrder.setCreatedAt(order.getCreatedAt());
            existingOrder.setUpdatedBy(order.getUpdatedBy());
            existingOrder.setUpdatedAt(order.getUpdatedAt());
            existingOrder.setAssignedTo(order.getAssignedTo());
            existingOrder.setStatus(order.getStatus());
            existingOrder.setTakenBy(order.getTakenBy());
            existingOrder.setTakenAt(order.getTakenAt());
            existingOrder.setDoneAt(order.getDoneAt());
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
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "Scooter",  ""));
        return order.getScooters();
    }

    public List<OrderScooter> getOrderScootersByOrderId(Long orderId) {
        return orderScooterRepository.findByOrderId(orderId);
    }

    public OrderScooter updatePriority(Long orderId, Long scooterId, Integer priority) {
        OrderScooterId id = new OrderScooterId(orderId, scooterId);
        OrderScooter orderScooter = orderScooterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderScooter", "Priority", ""));
        orderScooter.setPriority(priority);
        return orderScooterRepository.save(orderScooter);
    }

//    public Integer getPriority(Long orderId, Long scooterId) {
//        OrderScooterKey id = new OrderScooterKey(orderId, scooterId);
//        OrderScooter orderScooter = orderScooterRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("OrderScooter not found"));
//        return orderScooter.getPriority();
//    }

    public Integer getPriority(Long orderId, Long scooterId) {
        return orderScooterRepository.getPriorityByOrderIdAndScooterId(orderId, scooterId);
    }

}
