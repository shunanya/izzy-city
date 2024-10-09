package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.*;
import com.izzy.payload.response.TaskInfo;
import com.izzy.repository.NotificationRepository;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.TaskRepository;
import com.izzy.security.custom.service.CustomService;
import com.izzy.security.utils.Utils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final CustomService customService;
    private final OrderRepository orderRepository;
    private final ScooterRepository scooterRepository;
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;

    public TaskService(CustomService customService,
                       OrderRepository orderRepository,
                       ScooterRepository scooterRepository,
                       TaskRepository taskRepository,
                       NotificationRepository notificationRepository) {
        this.customService = customService;
        this.orderRepository = orderRepository;
        this.scooterRepository = scooterRepository;
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Retrieve tasks by filtering
     *
     * @param viewType   optional parameter to get 'simple'(aka origin), 'short' or 'detailed' task data view (default is 'simple')
     * @param orderId    optional order Id which is owner of task
     * @param scooterId  optional scooter ID associated with the task
     * @param priorities optional priorities of task (concrete data or range of data)
     * @param status     optional status of task
     * <p>
     *   {@code status} is alternative for {@code priorities} so that one of them should be defined only.
     * </p>
     * @return List of filtered tasks
     */
    public List<?> getTasksByFiltering(String viewType,
                                       Long orderId,
                                       Long scooterId,
                                       String priorities,
                                       String status) {
        List<Integer> priorityRange = new ArrayList<>(2);
        if (status != null && !status.isBlank()) {
            Task.Status st = Task.Status.getStatusByString(status);
            if (st == null) throw new UnrecognizedPropertyException("status", status);
            else if (st == Task.Status.ACTIVE) {
                priorityRange.add(0, 1);
                priorityRange.add(1, 100);
            } else {
                Integer priority = st.getValue();
                priorityRange.add(priority);
                priorityRange.add(priority);
            }
        } else {
            priorityRange = Utils.parseDataRangeToPairOfInteger(priorities, -1, 100);
        }
        List<Task> tasks = taskRepository.findTasksByFiltering(orderId, scooterId, priorityRange.get(0), priorityRange.get(1));
        return switch (viewType == null ? "simple" : viewType) {
            case "short" ->
                    tasks.stream().map(task -> new TaskInfo(getOrderByOrderId(task.getOrderId()), getScooterByScooterId(task.getScooterId()), task, true)).collect(Collectors.toList());
            case "detailed" ->
                    tasks.stream().map(task -> new TaskInfo(getOrderByOrderId(task.getOrderId()), getScooterByScooterId(task.getScooterId()), task, false)).collect(Collectors.toList());
            default -> tasks;
        };
    }

    /**
     * Appending a new task to the existing tasks
     *
     * @param orderId existing order id that contains tasks to be updated.
     * @param taskDTO a task to be appending
     * @return updated list of tasks
     * @throws AccessDeniedException    if operation is not permitted for current user
     * @throws IllegalArgumentException if provided task has not valid structure
     * @throws BadRequestException      if provided task is already exists in the order tasks list
     */
    @Transactional
    public List<Task> appendTask(@NonNull Long orderId, @NonNull TaskDTO taskDTO) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to append task to order created with user above your role");
        if (taskDTO.getScooterId() == null)
            throw new IllegalArgumentException("Task lacks a scooter ID.");
        if (taskDTO.getOrderId() != null && !orderId.equals(taskDTO.getOrderId())) {
            throw new BadRequestException("The provided task and order are mismatched.");
        }

        taskDTO.setOrderId(orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        List<Task> tasks = order.getTasks();
        boolean updated = false;
        for (Task t : tasks)
            if (t.getScooterId().equals(taskDTO.getScooterId()))
                if (t.getPriority() <= 0 || t.getPriority().equals(taskDTO.getPriority()))
                    throw new BadRequestException("Given task is already included");
                else { // Identical task found but with a different priority
                    t.setPriority(taskDTO.getPriority()); // change priority
                    updated = true;
                    break;
                }
        // identical task not found - add a new one
        if (!updated) tasks.add(new Task(taskDTO));

//        taskRepository.deleteAllByIdOrderId(tasks.get(0).getOrderId()); // remove old tasks
//        taskRepository.flush();
//        List<Task> keptTasks = Utils.rearrangeTasksPriorities(tasks);
        return taskRepository.saveAll(Utils.rearrangeTasksPriorities(tasks)); // store tasks
    }

    /**
     * Mark the task as complete
     *
     * @param orderId existing order id that contains tasks to be updated.
     * @param taskDTO a task to be marked as completed
     * @return updated list of tasks
     */
    @Transactional
    public List<Task> markTaskAsCompleted(Long orderId, TaskDTO taskDTO) {
        if (taskDTO.getOrderId() != null && !orderId.equals(taskDTO.getOrderId())) {
            throw new BadRequestException("The provided task and order are mismatched.");
        }
        taskDTO.setOrderId(orderId);
        return markTask(orderId, new Task(taskDTO), Task.Status.COMPLETED);
    }

    @Transactional
    public List<Task> markTaskAsCanceled(Long orderId, TaskDTO taskDTO) {
        if (taskDTO.getOrderId() != null && !orderId.equals(taskDTO.getOrderId())) {
            throw new BadRequestException("The provided task and order are mismatched.");
        }
        taskDTO.setOrderId(orderId);
        return markTask(orderId, new Task(taskDTO), Task.Status.CANCELED);
    }


    private List<Task> markTask(@NonNull Long orderId, @NonNull Task task, Task.Status status) {
        // task must include at least the scooterId
        if (!task.hasScooterId()) throw new IllegalArgumentException("Task lacks scooter ID.");
        if (task.getOrderId() != null && !task.getOrderId().equals(orderId))
            throw new BadRequestException("The provided task and order are mismatched.");
        Long userManager = customService.currentUserHeadId();
        if (userManager == null)
            throw new AccessDeniedException("Only Executors having Scout or Charger roles can mark task.");
        // get existing order
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        List<Task> tasks = order.getTasks();
        Task updatedTask = null;
        Long id = task.getScooterId();

        for (Task t : tasks) // find the specified task
            if (t.getScooterId().equals(id))
                if (t.getTaskStatus() != status) { // Is the task already marked with the specified status?
                    switch (status) {
                        case COMPLETED -> {
                            t.setCompleted();
                            t.setComment(task.getComment() == null || task.getComment().isBlank() ? "Task is completed" : task.getComment());
                        }
                        case CANCELED -> {
                            if (task.getComment() == null || task.getComment().isBlank()) {
                                throw new BadRequestException("Comments should be given when a task is canceled.");
                            }
                            t.setCanceled();
                            t.setComment(task.getComment());
                        }
                        case ACTIVE -> t.setActive();
                        default ->
                                throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", status));
                    }
                    if (List.of(Task.Status.CANCELED, Task.Status.COMPLETED).contains(status)) {
                        // create notification for user-manager about change the task status
                        notificationRepository.save(new Notification(null, customService.currentUserHeadId(), t.getOrderId(), t.getScooterId()));
                    } else {
                        // remove unnecessary notification (if exist)
                        notificationRepository.findNotificationByOrderIdAndScooterId(t.getOrderId(), t.getScooterId())
                                .ifPresent(notificationRepository::delete);
                    }
                    updatedTask = t;
                    break;
                } else throw new BadRequestException("Task already marked as " + status);
        if (updatedTask == null)
            throw new ResourceNotFoundException("Task", "", String.format("{orderId: %s, ScooterId: %s}", orderId, task.getScooterId()));

//        taskRepository.deleteAllByIdOrderId(tasks.get(0).getOrderId()); // remove old tasks
//        taskRepository.flush();
//        List<Task> keptTasks = Utils.rearrangeTasksPriorities(tasks);
        return taskRepository.saveAll(Utils.rearrangeTasksPriorities(tasks)); // store tasks
    }

    /**
     * Completes List of raw Task {@link Task}
     *
     * @param rawTasks List of raw Task to be completed
     * @param order    existing Order that has to own of these tasks
     * @return the List of Tasks
     */
    public List<Task> completeTasks(@NonNull List<TaskDTO> rawTasks, @NonNull Order order) {
        List<Task> tasks = new ArrayList<>();
        if (order.isValid()) for (TaskDTO taskDTO : rawTasks) tasks.add(createTaskFromDTO(taskDTO, order));
        else throw new IllegalArgumentException("Task or Order has not valid structure.");
        return tasks;
    }

    public Task createTaskFromDTO(TaskDTO rawTask, Order order) {
        rawTask.setOrderId(order.getId());
        if (rawTask.isValid()) {
            Task task = new Task(rawTask);
/*
            task.setOrder(order);
            scooterRepository.findById(task.getScooterId()).ifPresentOrElse(task::setScooter, () -> {
                throw new ResourceNotFoundException("Scooter", "id", task.getScooterId());
            });
*/
            return task;
        } else throw new IllegalArgumentException("Task or Order has not valid structure.");
    }

    @Transactional
    public void removeTask(@NonNull Long orderId, @NonNull TaskDTO taskDTO) {
        if (!customService.checkAllowability(orderId))
            throw new AccessDeniedException("not allowed to remove task in order created with user above your role");
        if (taskDTO.getOrderId() != null && !taskDTO.getOrderId().equals(orderId))
            throw new BadRequestException("The provided task and order are mismatched.");
        taskDTO.setOrderId(orderId);
        Long scooterId = taskDTO.getScooterId();
        taskRepository.findByOrderAndScooterIds(orderId, scooterId).
                ifPresentOrElse(t -> taskRepository.deleteByOrderAndScooterIds(orderId, scooterId),
                        () -> {
                            throw new ResourceNotFoundException("Task", "", String.format("{orderId: %s, scooterId: %s}", orderId, scooterId));
                        });
    }

    private Order getOrderByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (!customService.checkAllowability(order))
            throw new AccessDeniedException("not allowed to get tasks from order created with user above your role");
        return order;
    }

    private Scooter getScooterByScooterId(Long scooterId) {
        return scooterRepository.findById(scooterId).orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", scooterId));
    }

    public List<Task> getTasksByOrderId(@NonNull Long orderId) {
        return getOrderByOrderId(orderId).getTasks();
    }

    public List<TaskInfo> getShortTaskInfosByOrderId(Long orderId) {
        return getTaskInfosByOrder(getOrderByOrderId(orderId), true);
    }

    public List<TaskInfo> getDetailedTaskInfosByOrderId(Long orderId) {
        return getTaskInfosByOrder(getOrderByOrderId(orderId), false);
    }

    public List<TaskInfo> getTaskInfosByOrder(Order order, Boolean shortInfo) {
        List<Task> tasks = order.getTasks();
        return tasks.stream().map(task -> new TaskInfo(order, getScooterByScooterId(task.getScooterId()), task, shortInfo)).collect(Collectors.toList());
    }

    private List<Order> getOrdersByAssignedToUserId(Long assignedToUserId) {
        if (!customService.checkAllowability(assignedToUserId, true))
            throw new AccessDeniedException("not allowed to get order not assigned you");
        return orderRepository.findOrdersByFilters(null, null, null, assignedToUserId);
    }

    public List<Task> getTasksByAssigned(Long assignedToUserId) {
        List<Order> orders = getOrdersByAssignedToUserId(assignedToUserId);

        List<Task> tasks = new ArrayList<>();
        if (!orders.isEmpty()) orders.forEach(order -> {
            List<Task> orderTasks = order.getTasks();
            if (!orderTasks.isEmpty()) tasks.addAll(orderTasks);
        });
        return Utils.rearrangeTasksPriorities(tasks);
    }

    private List<TaskInfo> getTaskInfosByAssigned(Long assignedToUserId, Boolean shortView) {
        List<Order> orders = getOrdersByAssignedToUserId(assignedToUserId);

        if (!orders.isEmpty())
            return orders.stream().map(order -> getTaskInfosByOrder(order, shortView)).
                    filter(orderTaskInfos -> !orderTaskInfos.isEmpty()).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
        return new ArrayList<>();
    }

    public List<TaskInfo> getShortTaskInfosByAssigned(Long assignedToUserId) {
        return getTaskInfosByAssigned(assignedToUserId, true);
    }

    public List<TaskInfo> getDetailedTaskInfosByAssigned(Long assignedToUserId) {
        return getTaskInfosByAssigned(assignedToUserId, false);
    }

    private List<Order> getOrdersAssignedMe() {
        Long userId = customService.currentUserId();
        return orderRepository.findOrdersByFilters(null, null, null, userId);
    }

    public List<Task> getTasksAssignedMe() {
        List<Order> orders = getOrdersAssignedMe();
        List<Task> tasks = new ArrayList<>();
        if (!orders.isEmpty())
            tasks = orders.stream().map(Order::getTasks).
                    filter(orderTasks -> !orderTasks.isEmpty()).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
        return Utils.rearrangeTasksPriorities(tasks);
    }

    private List<TaskInfo> getTaskInfosAssignedMe(Boolean shortView) {
        List<Order> orders = getOrdersAssignedMe();
        if (!orders.isEmpty())
            return orders.stream().map(order -> getTaskInfosByOrder(order, shortView)).
                    filter(orderTaskInfos -> !orderTaskInfos.isEmpty()).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
        return new ArrayList<>();
    }

    public List<TaskInfo> getShortTaskInfosAssignedMe() {
        return getTaskInfosAssignedMe(true);
    }

    public List<TaskInfo> getDetailedTaskInfosAssignedMe() {
        return getTaskInfosAssignedMe(false);
    }

    public List<Task> rearrangeTaskPriorities(Long orderId) {
        return Utils.rearrangeTasksPriorities(getTasksByOrderId(orderId));
    }

/*
    @Transactional
    public Task updatePriority(Long orderId, Long scooterId, Integer priority) {
        Task task = taskRepository.deleteByOrderAndScooterIds(orderId, scooterId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "Priority", priority));
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    public Integer getPriority(Long orderId, Long scooterId) {
        return taskRepository.getPriorityByOrderIdAndScooterId(orderId, scooterId);
    }
*/

}
