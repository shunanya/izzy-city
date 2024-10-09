package com.izzy.repository;

import com.izzy.model.Task;
import com.izzy.model.TaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, TaskId> {

    /**
     * Retrieves all tasks attached to order
     *
     * @param orderId the order id
     * @return the list of tasks
     */
    List<Task> findByIdOrderId(Long orderId);

    /**
     * Retrieves all tasks where specified scooter is used
     *
     * @param scooterId the scooter id
     * @return the list of tasks
     */
    List<Task> findAllByIdScooterId(@Param("scooterId") @NonNull Long scooterId);
//    List<Task> findAllByScooterId(@Param("scooterId") @NonNull Long scooterId);

    /**
     * Retrieves List of objects for defined user
     *
     * @param userManagerId id for user manger
     * @return list of objects [orderId, scooterId, priority]
     */
    @Query("SELECT t FROM Task t JOIN Order o ON t.id.orderId = o.id JOIN User u ON o.assignedTo = u.id WHERE u.userManager = :userManagerId")
    List<Task> findTasksByUserManagerId(@Param("userManagerId") @NonNull Long userManagerId);

    @Query("SELECT t FROM Task t JOIN Order o ON t.id.orderId = o.id JOIN User u ON o.assignedTo = u.id WHERE u.userManager = :userManagerId AND t.priority <= 0")
    List<Task> findNonActiveTasksByUserManagerId(@Param("userManagerId") @NonNull Long userManagerId);

    @Query("SELECT t FROM Task t JOIN Order o ON t.id.orderId = o.id WHERE o.assignedTo = :assignedUserId")
    List<Task> findByAssignedUserId(@Param("assignedUserId") @NonNull Long assignedUserId);

    @Query("SELECT t FROM Task t JOIN Order o ON t.id.orderId = o.id WHERE o.assignedTo = :assignedUserId AND (:priorities IS NULL OR t.priority IN :priorities)")
    List<Task> findFilteredTasksByAssignedUserId(@Param("assignedUserId") @NonNull Long assignedUserId,
                                                 @Param("priorities") @Nullable List<Integer> priorities);

    @Query("SELECT t FROM Task t JOIN Order o ON t.id.orderId = o.id WHERE o.assignedTo = :assignedUserId AND t.priority <= 0")
    List<Task> findAllNonActiveTasksByAssignedUserId(@Param("assignedUserId") @NonNull Long assignedUserId);

    @Query("SELECT t FROM Task t WHERE t.id.orderId = :orderId AND t.id.scooterId = :scooterId")
    Optional<Task> findByOrderAndScooterIds(@Param("orderId") @NonNull Long orderId, @Param("scooterId") @NonNull Long scooterId);

    Optional<Task> findByIdOrderIdAndIdScooterId(@Param("orderId") @NonNull Long orderId, @Param("scooterId") @NonNull Long scooterId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.id.orderId = :orderId AND t.id.scooterId = :scooterId")
    int deleteByOrderAndScooterIds(@Param("orderId") @NonNull Long orderId, @Param("scooterId") @NonNull Long scooterId);

    int deleteTaskById(@Param("TaskId") @NonNull TaskId id);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.id.orderId = :orderId")
    int deleteAllByIdOrderId(@Param("orderId") @NonNull Long orderId);

    int deleteTasksByIdOrderId(@Param("orderId") @NonNull Long orderId);

    @Query("SELECT t.priority FROM Task t WHERE t.id.orderId = :orderId AND t.id.scooterId = :scooterId")
    Integer getPriorityByOrderIdAndScooterId(@Param("orderId") Long orderId, @Param("scooterId") Long scooterId);

    @Query("SELECT t FROM Task t " +
            "WHERE " +
            "(:orderId IS NULL OR t.id.orderId = :orderId) AND " +
            "(:scooterId IS NULL OR t.id.scooterId = :scooterId) AND " +
            "(:minPriority IS NULL OR t.priority >= :minPriority) AND " +
            "(:maxPriority IS NULL OR t.priority <= :maxPriority)")
    List<Task> findTasksByFiltering(@Param("orderId") @Nullable Long orderId, @Param("scooterId") @Nullable Long scooterId,
                                    @Param("minPriority") @Nullable Integer minPriority, @Param("maxPriority") @Nullable Integer maxPriority);
}