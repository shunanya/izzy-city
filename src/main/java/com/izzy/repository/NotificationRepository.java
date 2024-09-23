package com.izzy.repository;


import com.izzy.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by user ID.
     *
     * @param userId the user ID to search for
     * @return List of Notifications associated with the given user ID
     */
    List<Notification> findAllByUserId(@Param("userId") @NonNull Long userId);

    /**
     * Find notifications by user action.
     *
     * @param userAction the user action to search for
     * @return List of Notifications with the given user action
     */
    List<Notification> findAllByUserAction(@Param("userAction") @NonNull String userAction);

    /**
     * Get notification via unique pair (orderId and scooterId)
     * @param orderId the order id
     * @param scooterId the scooter id
     * @return optional notification
     */
    Optional<Notification> findNotificationByOrderIdAndScooterId(@Param("orderId") @NonNull Long orderId, @Param("scooterId") @NonNull Long scooterId);

    /**
     * Find filtered notifications
     * @param userId optional userId
     * @param action optional user action
     * @param priority optional task priority.
     * <p>
     *   Note: priority can be obtained via task status: {@code priority = Task.Status.getStatusByString(taskStatus).getValue()}
     * </p>
     * @return List notifications
     */
    @Query("SELECT n FROM Notification n JOIN Task t ON (t.id.orderId = n.orderId AND t.id.scooterId = n.scooterId) WHERE " +
            "(:userId IS NULL OR n.userId = :userId) AND " +
            "(:action IS NULL OR n.userAction ILIKE %:action%) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    List<Notification> findNotificationsByFilters(@Param("userId") @Nullable Long userId,
                                                  @Param("action") @Nullable String action,
                                                  @Param("priority") @Nullable Integer priority);

}
