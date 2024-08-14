package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.model.OrderScooterId;
import com.izzy.model.Scooter;
import com.izzy.model.misk.Task;
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
    private final ScooterRepository scooterRepository;
    private final OrderScooterRepository orderScooterRepository;

    public TaskService(CustomService customService, ScooterRepository scooterRepository, OrderScooterRepository orderScooterRepository) {
        this.customService = customService;
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
        List<OrderScooter> orderScooters = getOrderScootersByOrderId(orderId);
        if (orderScooters != null && !orderScooters.isEmpty()) {
            OrderScooter newOrderScooter = convertTaskToOrderScooter(task, orderScooters.get(0).getOrder());
            for (OrderScooter os : orderScooters) {
                if (newOrderScooter.equals(os)) {
                    if (newOrderScooter.getPriority().equals(os.getPriority())) {
                        throw new BadRequestException("Given task is already included");
                    } else {
                        os.setPriority(newOrderScooter.getPriority());
                        newOrderScooter = null;
                        break;
                    }
                }
            }
            if (newOrderScooter != null) orderScooters.add(newOrderScooter);
        } else {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
        orderScooterRepository.saveAll(Utils.rearrangeOrderScooterPriorities(orderScooters)); // update tasks
        return Utils.convertOrderScooterToTasks(orderScooters);
    }

    @Transactional
    public List<Task> removeTask(@NonNull Long orderId, @NonNull Long scooterId) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to remove task in order created with user above your role");
        Optional<OrderScooter> optionalOrderScooter = orderScooterRepository.findByOrderAndScooterIds(orderId, scooterId);
        optionalOrderScooter.ifPresentOrElse(
                os -> orderScooterRepository.deleteByOrderAndScooterIds(os.getOrder().getId(), os.getScooter().getId()),
                () -> {
                    throw new ResourceNotFoundException("OrderScooter", "Task", new OrderScooterId(orderId, scooterId));
                });
        return Utils.convertOrderScooterToTasks(Utils.rearrangeOrderScooterPriorities(orderScooterRepository.findByOrderId(orderId)));
    }

    @Transactional
    public List<Task> removeTask(@NonNull Long orderId, @NonNull Task task) {
        return removeTask(orderId, task.getScooterId());
    }

    public List<OrderScooter> getOrderScootersByOrderId(@NonNull Long orderId) {
        return orderScooterRepository.findByOrderId(orderId);
    }

    private OrderScooter convertTaskToOrderScooter(@NonNull Task task, @NonNull Order order) {
        if (task.isValid() && order.isValid()) {
            OrderScooter orderScooter = new OrderScooter();
            orderScooter.setId(new OrderScooterId(order.getId(), task.getScooterId()));
            orderScooter.setOrder(order);
            Scooter scooter = scooterRepository.findById(task.getScooterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", task.getScooterId()));
            orderScooter.setScooter(scooter);
            orderScooter.setPriority(task.getPriority());
            return orderScooter;
        } else {
            throw new IllegalArgumentException("Task or Order has not valid structure.");
        }
    }

    public List<OrderScooter> convertTasksToOrderScooters(@NonNull List<Task> tasks, @NonNull Order order) {
        List<OrderScooter> orderScooters = new ArrayList<>();
        for (Task task : tasks) {
            orderScooters.add(convertTaskToOrderScooter(task, order));
        }
        return orderScooters;
    }

    public List<Task> getTasksByOrderId(@NonNull Long orderId) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to get tasks from order created with user above your role");
        return Utils.convertOrderScooterToTasks(getOrderScootersByOrderId(orderId));
    }

    public List<OrderScooter> rearrangeOrderScooterPriorities(Long orderId) {
        return Utils.rearrangeOrderScooterPriorities(getOrderScootersByOrderId(orderId));
    }


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

}
