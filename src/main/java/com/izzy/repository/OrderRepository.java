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
    Optional<Order> findById(Long id);

    @Override
    boolean existsById(Long id);

    @Override
    void deleteById(Long id);

    @Override
    void delete(Order entity);

    @Query("SELECT o FROM Order o WHERE " +
            "(:action IS NULL OR o.action ILIKE %:action%) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:createdBy IS NULL OR o.createdBy = :createdBy) AND " +
            "(:assignedTo IS NULL OR o.assignedTo = :assignedTo)")
    List<Order> findOrdersByFilters(@Param("action") String action,
                                    @Param("status") String status,
                                    @Param("createdBy") Long createdBy,
                                    @Param("assignedTo") Long assignedTo);
}