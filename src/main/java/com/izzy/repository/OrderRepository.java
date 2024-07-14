package com.izzy.repository;

import com.izzy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    Optional<Order> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(Order entity);

    @Query("SELECT o FROM Order o WHERE " +
            "(:action IS NULL OR o.action LIKE %:action%) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:createdBy IS NULL OR o.created_by.id = :createdBy) AND " +
            "(:assignedTo IS NULL OR o.assigned_to.id = :assignedTo)")
    List<Order> findOrdersByFilters(@Param("action") String action,
                                    @Param("status") String status,
                                    @Param("createdBy") Long createdBy,
                                    @Param("assignedTo") Long assignedTo);
}