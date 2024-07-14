package com.izzy.service;

import com.izzy.model.OrderEntity;
import com.izzy.model.OrderScooterEntity;
import com.izzy.model.OrderScooterId;
import com.izzy.model.ScooterEntity;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderScooterRepository orderScooterRepository;

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderEntity> getOrders(String action, String status, Long createdBy, Long assignedTo) {
        if (action != null || status != null || createdBy != null || assignedTo != null) {
            return orderRepository.findOrdersByFilters(action, status, createdBy, assignedTo);
        } else {
            return orderRepository.findAll();
        }
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public OrderEntity createOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    public OrderEntity updateOrder(Long id, OrderEntity order) {
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

    public Set<ScooterEntity> getScootersByOrderId(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getScooters();
    }

    public List<OrderScooterEntity> getOrderScootersByOrderId(Long orderId) {
        return orderScooterRepository.findByOrderId(orderId);
    }

    public OrderScooterEntity updatePriority(Long orderId, Long scooterId, Integer priority) {
        OrderScooterId id = new OrderScooterId(orderId, scooterId);
        OrderScooterEntity orderScooter = orderScooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderScooter not found"));
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
