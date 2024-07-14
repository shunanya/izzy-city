package com.izzy.repository;

import com.izzy.model.OrderScooterEntity;
import com.izzy.model.OrderScooterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderScooterRepository extends JpaRepository<OrderScooterEntity, OrderScooterId> {
    List<OrderScooterEntity> findByOrderId(Long orderId);

    @Override
    Optional<OrderScooterEntity> findById(OrderScooterId orderScooterId);

    @Override
    boolean existsById(OrderScooterId orderScooterId);

    @Query("SELECT os.priority FROM OrderScooterEntity os WHERE os.id.order_id = :orderId AND os.id.scooter_id = :scooterId")
    Integer getPriorityByOrderIdAndScooterId(@Param("orderId") Long orderId, @Param("scooterId") Long scooterId);

}