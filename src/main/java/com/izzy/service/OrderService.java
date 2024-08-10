package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.CustomException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.*;
import com.izzy.payload.misk.Task;
import com.izzy.payload.request.OrderRequest;
import com.izzy.payload.response.OrderInfo;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.UserRepository;
import com.izzy.security.custom.service.CustomService;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<?> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (!orders.isEmpty()) {
            List<OrderInfo> orderInfos = new ArrayList<>();
            orders.forEach(order -> { // filtering permitted orders for current user
                if (checkAllowability(order)) {
                    orderInfos.add(convertOrderToOrderInfo(order));
                }
            });
            return orderInfos;
        }
        return orders;
    }

    /**
     * Converts the provided request data into Order entity
     *
     * @param orderRequest the provided data {@link OrderRequest}
     * @param orderId      Should be Null on creation.
     * @return entity Order on success
     */
    public OrderInfo getOrderInfoFromOrderRequest(OrderRequest orderRequest, Long orderId) {
        boolean creation = (orderId == null);
        OrderInfo orderInfo = new OrderInfo();

        if (!creation) orderInfo.setId(orderId);
        String tmp = orderRequest.getName();
        if (tmp != null && !tmp.isBlank()) orderInfo.setName(tmp);
        tmp = orderRequest.getDescription();
        if (tmp != null && !tmp.isBlank()) orderInfo.setDescription(tmp);
        tmp = orderRequest.getAction();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Action.checkByValue(tmp)) orderInfo.setAction(tmp);
            else
                throw new IllegalArgumentException(String.format("Error: action field contains illegal value '%s'", tmp));
        } else if (creation) {
            throw new BadRequestException("Action must be defined while creating order");
        }
        if (creation) {
            orderInfo.setCreatedBy(customService.currentUserId());
            orderInfo.setCreatedAt(Timestamp.from(Instant.now()));
        }
        Long id = orderRequest.getAssignedTo();
        if (id != null) {
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                if (customService.checkAllowability(existingUser.get())) {
                    orderInfo.setAssignedTo(existingUser.get().getId());
                } else throw new AccessDeniedException("not allowed to assign order to user with above your role");
            }
        }
        tmp = orderRequest.getStatus();
        if (tmp != null && !tmp.isBlank()) {
            if (OrderRequest.Status.checkByValue(tmp)) orderInfo.setStatus(tmp);
            else
                throw new IllegalArgumentException(String.format("status field contains illegal value '%s'", tmp));
        } else if (creation) orderInfo.setStatus(OrderRequest.Status.CREATED.getValue());
        id = orderRequest.getTakenBy();
        if (id != null && !creation) { // Only in the update request.
            Optional<User> existingUser = userRepository.findById(id);
            existingUser.ifPresent(u -> orderInfo.setTakenBy(u.getId()));
        }
        Timestamp ts = orderRequest.getTakenAt();
        if (ts != null && orderId != null) orderInfo.setTakenAt(ts); // Only in the update request.
        ts = orderRequest.getDoneAt();
        if (ts != null && orderId != null) orderInfo.setDoneAt(ts); // Only in the update request.


        List<Task> tasks = orderRequest.getTasks();
        if (tasks != null && !tasks.isEmpty()) {
            tasks.forEach(task -> {
                if (!scooterRepository.existsById(task.getScooterId()))
                    throw new IllegalArgumentException(String.format("Error: Scooter with id='%s' not found", task.getScooterId()));
            });
            orderInfo.setTasks(tasks);
        } else if (creation) throw new BadRequestException("Order must include at least one task.");

        return orderInfo;
    }

    private boolean checkAllowability(@NonNull Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) throw new ResourceNotFoundException("Order", "id", orderId);
        return checkAllowability(orderOptional.get());
    }

    private boolean checkAllowability(@NonNull Order order) {
        Long createdUserId = order.getCreatedBy();
        Optional<User> user = userRepository.findById(createdUserId);
        if (createdUserId == null || user.isEmpty())
            throw new CustomException(500, "Error: Order with erroneous 'createdBy' field: " + createdUserId);
        return customService.checkAllowability(user.get());
    }

    public OrderInfo getOrderInfoByOrderId(@NotNull Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order", "ID", orderId);
        }
        if (checkAllowability(orderOptional.get())) {
            return convertOrderToOrderInfo(orderOptional.get());
        } else throw new AccessDeniedException("not allowed to request order created with user above your role");
    }

    public OrderInfo convertOrderToOrderInfo(Order order) {
        return new OrderInfo(order);
    }

    public Order convertOrderInfoToOrder(@NotNull OrderInfo orderInfo) {
        Order order = new Order();
        Long id = orderInfo.getId();
        if (id != null) order.setId(id);
        order.setAction(orderInfo.getAction());
        order.setName(orderInfo.getName());
        order.setDescription(orderInfo.getDescription());
        order.setCreatedBy(orderInfo.getCreatedBy());
        order.setCreatedAt(orderInfo.getCreatedAt());
        order.setUpdatedBy(orderInfo.getUpdatedBy());
        order.setUpdatedAt(orderInfo.getUpdatedAt());
        order.setAssignedTo(orderInfo.getAssignedTo());
        order.setStatus(orderInfo.getStatus());
        order.setTakenBy(orderInfo.getTakenBy());
        order.setTakenAt(orderInfo.getTakenAt());
        order.setDoneAt(orderInfo.getDoneAt());
        List<OrderScooter> orderScooters = convertTasksToOrderScooters(orderInfo.getTasks(), order);

        order.setOrderScooters(orderScooters);
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
    public List<OrderInfo> getOrders(@Nullable String action,
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
        List<OrderInfo> orderInfos = new ArrayList<>();
        orders.forEach(order -> { // filtering permitted orders for current user
            if (checkAllowability(order)) {
                orderInfos.add(convertOrderToOrderInfo(order));
            }
        });
        return orderInfos;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    public OrderInfo saveOrderInfo(OrderInfo orderInfo) {
        Order order = convertOrderInfoToOrder(orderInfo);
        Order savedOrder = saveOrder(order);

        List<Task> tasks = orderInfo.getTasks();
        if (tasks != null && !tasks.isEmpty()) {
            orderScooterRepository.saveAll(convertTasksToOrderScooters(rearrangeTaskPriorities(tasks), savedOrder));
        }
        orderInfo.setId(order.getId());
        return orderInfo;
    }

    public List<OrderScooter> convertTasksToOrderScooters(@NonNull List<Task> tasks, @NonNull Order order) {
        List<OrderScooter> orderScooters = new ArrayList<>();
        if (tasks != null && !tasks.isEmpty() && order.getId() != null) {
            orderScooters = tasks.stream().map(task -> {
                OrderScooter orderScooter = new OrderScooter();
                orderScooter.setId(new OrderScooterId(order.getId(), task.getScooterId()));
                orderScooter.setOrder(order);
                Scooter scooter = scooterRepository.findById(task.getScooterId())
                        .orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", task.getScooterId()));
                orderScooter.setScooter(scooter);
                orderScooter.setPriority(task.getPriority());
                return orderScooter;
            }).collect(Collectors.toList());
        }
        return orderScooters;
    }

    private OrderScooter convertTaskToOrderScooter(@NonNull Task task, @NonNull Order order) {
        OrderScooter orderScooter = new OrderScooter();
        orderScooter.setId(new OrderScooterId(order.getId(), task.getScooterId()));
        orderScooter.setOrder(order);
        Scooter scooter = scooterRepository.findById(task.getScooterId())
                .orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", task.getScooterId()));
        orderScooter.setScooter(scooter);
        orderScooter.setPriority(task.getPriority());
        return orderScooter;
    }

    public List<Task> convertOrderScooterToTasks(List<OrderScooter> orderScooters) {
        List<Task> tasks = new ArrayList<>();
        if (orderScooters != null && !orderScooters.isEmpty()) {
            tasks = orderScooters.stream().map(os -> new Task(os.getScooter().getId(), os.getPriority())).collect(Collectors.toList());
        }
        return tasks;
    }

    private Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Long updateOrderInfo(@NonNull Long id, @NonNull OrderInfo orderInfo) {
        Order order = prepareToUpdateOrder(id, orderInfo);
        if (!checkAllowability(order))
            throw new AccessDeniedException("not allowed to update order created with user above your role");

        Order savedOrder = orderRepository.save(order);


        List<Task> tasks = orderInfo.getTasks();
        if (tasks != null && !tasks.isEmpty()) { // new tasks are given
            orderScooterRepository.deleteByOrderId(savedOrder.getId()); // remove old tasks
            orderScooterRepository.saveAll(convertTasksToOrderScooters(rearrangeTaskPriorities(tasks), savedOrder)); // add new tasks
        }
        return id;
    }

    private Order prepareToUpdateOrder(@NonNull Long id, @NonNull OrderInfo orderInfo) {
        return orderRepository.findById(id).map(existingOrder -> {
            String tmp = orderInfo.getAction();
            if (tmp != null && !tmp.isBlank()) existingOrder.setAction(tmp);
            tmp = orderInfo.getName();
            if (tmp != null && !tmp.isBlank()) existingOrder.setName(tmp);
            tmp = orderInfo.getDescription();
            if (tmp != null && !tmp.isBlank()) existingOrder.setDescription(tmp);
            existingOrder.setUpdatedBy(customService.currentUserId());
            existingOrder.setUpdatedAt(Timestamp.from(Instant.now()));
            Long u = orderInfo.getAssignedTo();
            if (u != null) {
                Optional<User> existingUser = userRepository.findById(u);
                if (existingUser.isPresent()) {
                    if (customService.checkAllowability(existingUser.get())) {
                        existingOrder.setAssignedTo(u);
                        existingOrder.setStatus(OrderRequest.Status.ASSIGNED.getValue());
                    } else throw new AccessDeniedException("not allowed to assign order to user with above your role");
                } else throw new ResourceNotFoundException("User", "AssignTo", u);
            } else {
                tmp = orderInfo.getStatus();
                if (tmp != null && !tmp.isBlank()) existingOrder.setStatus(tmp);
            }
            u = orderInfo.getTakenBy();
            if (u != null) {
                Optional<User> existingUser = userRepository.findById(u);
                if (existingUser.isPresent()) {
                    if (customService.checkAllowability(existingUser.get())) {
                        existingOrder.setAssignedTo(u);
                        existingOrder.setStatus(OrderRequest.Status.IN_PROGRESS.getValue());
                    } else
                        throw new AccessDeniedException("not allowed to set taking order to user with above your role");
                } else throw new ResourceNotFoundException("User", "TakenBy", u);
            }
            Timestamp ts = orderInfo.getTakenAt();
            if (ts != null) existingOrder.setTakenAt(ts);
            ts = orderInfo.getDoneAt();
            if (ts != null) existingOrder.setDoneAt(ts);
            return existingOrder;
        }).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    public void deleteOrder(@NonNull Long orderId) {
        if (!checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to delete order created with user above your role");
        orderRepository.deleteById(orderId);
    }

    public List<Task> getTasksByOrderId(@NonNull Long orderId) {
        if (!checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to get tasks from order created with user above your role");
        return convertOrderScooterToTasks(getOrderScootersByOrderId(orderId));
//      return getOrderInfoByOrderId(orderId).getTasks();
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
        if (!checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to append task to order created with user above your role");
        List<OrderScooter> orderScooters = getOrderScootersByOrderId(orderId);
        if (orderScooters != null) {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isPresent()) {
                OrderScooter newOrderScooter = convertTaskToOrderScooter(task, orderOptional.get());
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
            orderScooterRepository.saveAll(rearrangeOrderScooterPriorities(orderScooters)); // update tasks
        }
        return convertOrderScooterToTasks(orderScooters);
    }

    @Transactional
    public List<Task> removeTask(@NonNull Long orderId, @NonNull Long scooterId) {
        if (!checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to remove task in order created with user above your role");
        Optional<OrderScooter> optionalOrderScooter = orderScooterRepository.findByOrderAndScooterIds(orderId, scooterId);
        optionalOrderScooter.ifPresentOrElse(
                os -> orderScooterRepository.deleteByOrderAndScooterIds(os.getOrder().getId(), os.getScooter().getId()),
                () -> {
                    throw new ResourceNotFoundException("OrderScooter", "Task", new OrderScooterId(orderId, scooterId));
                });
        return convertOrderScooterToTasks(rearrangeOrderScooterPriorities(orderScooterRepository.findByOrderId(orderId)));
    }

    @Transactional
    public List<Task> removeTask(@NonNull Long orderId, @NonNull Task task) {
        return removeTask(orderId, task.getScooterId());
    }

    public List<Task> rearrangeTaskPriorities(@NonNull List<Task> tasks) {
        if (tasks.size() > 1) {
            tasks.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).setPriority(i + 1);
            }
        }
        return tasks;
    }

    public List<OrderScooter> rearrangeOrderScooterPriorities(@NonNull List<OrderScooter> orderScooters) {
        if (orderScooters.size() > 1) {
            orderScooters.sort((a, b) -> a.getPriority().compareTo(b.getPriority()));
            for (int i = 0; i < orderScooters.size(); i++) {
                orderScooters.get(i).setPriority(i + 1);
            }
        }
        return orderScooters;
    }

    public List<OrderScooter> rearrangeOrderScooterPriorities(Long orderId) {
        return rearrangeOrderScooterPriorities(getOrderScootersByOrderId(orderId));
    }

    public List<OrderScooter> getOrderScootersByOrderId(@NonNull Long orderId) {
        return orderScooterRepository.findByOrderId(orderId);
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
