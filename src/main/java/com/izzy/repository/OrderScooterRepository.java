package com.izzy.repository;

import com.izzy.model.OrderScooter;
import com.izzy.model.OrderScooterId;
import org.springframework.data.jpa.repository.JpaRepository;
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

    @Override
    boolean existsById(OrderScooterId orderScooterId);

    @Query("SELECT os FROM OrderScooter os WHERE os.id.order_id = :orderId AND os.id.scooter_id = :scooterId")
    Optional<OrderScooter> findByOrderIdScooterId(Long orderId, Long scooterId);

    @Query("SELECT os.priority FROM OrderScooter os WHERE os.id.order_id = :orderId AND os.id.scooter_id = :scooterId")
    Integer getPriorityByOrderIdAndScooterId(@Param("orderId") Long orderId, @Param("scooterId") Long scooterId);

}