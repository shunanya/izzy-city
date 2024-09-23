package com.izzy.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.model.Task;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create and configure ObjectMapper
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Test
    @Order(1)
    void findTasksByUserId() {
        List<Task> taskList = taskRepository.findTasksByUserManagerId(3L);

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
    }

    @Test
    @Order(2)
    void findTasksByAssignedUserId() {
        List<Task> taskList = taskRepository.findByAssignedUserId(6L);

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
        assertTrue(taskList.size() > 5);
    }

    @Test
    @Order(3)
    void findFilteredTasksByAssignedUserId() {
//        List<Task> taskList = taskRepository.findFilteredTasksByAssignedUserId(6L, new ArrayList<>(){{add(Task.Status.CANCELED.getValue());}});
        List<Task> taskList = taskRepository.findAllNonActiveTasksByAssignedUserId(6L);

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
    }

    @Test
    @Order(4)
    void findFilteredTasksByAssignedUserId_Without_Status_Defined() {
        List<Task> taskList = taskRepository.findFilteredTasksByAssignedUserId(6L, null);

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
    }

    @Test
    @Order(5)
    void statusTaskCheck() {
        Long orderId = 53L;
        Long scooterId = 1L;
        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId).
                orElse(new Task(orderId, scooterId, Task.Status.CANCELED.getValue()));
        task1.setStatus(Task.Status.CANCELED.toString());
        taskRepository.save(task1);

        task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId).orElse(null);
        assertNotNull(task1);
        assertNotNull(task1.getStatus());
        assert ("canceled".equalsIgnoreCase(task1.getTaskStatusString()));

        task1.setStatus(Task.Status.ACTIVE.toString());
        taskRepository.save(task1);

        task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId).orElse(null);
        assertNotNull(task1);
        assertNotNull(task1.getStatus());
        assertTrue ("Active".equalsIgnoreCase(task1.getTaskStatusString()));
    }

    @Test
    @Order(6)
//    @Commit        // or @Rollback(false)
    void removeTaskAndCheck() {
        Long orderId = 53L;
        Long scooterId = 1L;

        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId)
                .orElse(new Task(orderId, scooterId, Task.Status.CANCELED.getValue()));
        assertNotNull(task1);
        taskRepository.save(task1);

        // Perform the delete operation
        int deletedRecords = taskRepository.deleteByOrderAndScooterIds(task1.getOrderId(), task1.getScooterId());

        // Verify that the task is deleted
        assertEquals(1, deletedRecords);
        task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId).orElse(null);
        assertNull(task1);
    }

}