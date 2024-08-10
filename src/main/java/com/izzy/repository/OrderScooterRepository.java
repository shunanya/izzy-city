package com.izzy.repository;

import com.izzy.model.OrderScooter;
import com.izzy.model.OrderScooterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderScooterRepository extends JpaRepository<OrderScooter, OrderScooterId> {

    List<OrderScooter> findByOrderId(Long orderId);

    List<OrderScooter> findByScooterId(Long scooterId);

    @Override
    Optional<OrderScooter> findById(OrderScooterId orderScooterId);

    @Override
    void deleteById(OrderScooterId orderScooterId);

    @Modifying
    @Query("DELETE FROM OrderScooter os WHERE os.order.id = :orderId AND os.scooter.id = :scooterId")
    void deleteByOrderAndScooterIds(Long orderId, Long scooterId);

    void deleteByOrderId(Long orderId);

    @Override
    boolean existsById(OrderScooterId orderScooterId);

    /**
     * Retrieve List of objects for defined user
     * @param userId id for user
     * @return list of objects [order, scooter, priority]
     */
    @Query("SELECT os.order, os.scooter, os.priority FROM OrderScooter os JOIN Order o ON os.order.id = o.id JOIN User u ON o.assignedTo = u.id WHERE u.id = :userId")
    List<Object[]> findByUserId(Long userId);

    @Query("SELECT os FROM OrderScooter os WHERE os.order.id = :orderId AND os.scooter.id = :scooterId")
    Optional<OrderScooter> findByOrderAndScooterIds(Long orderId, Long scooterId);

    @Query("SELECT os.priority FROM OrderScooter os WHERE os.order.id = :orderId AND os.scooter.id = :scooterId")
    Integer getPriorityByOrderIdAndScooterId(@Param("orderId") Long orderId, @Param("scooterId") Long scooterId);

}