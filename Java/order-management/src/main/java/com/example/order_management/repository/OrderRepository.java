package com.example.order_management.repository;

import com.example.order_management.model.Order;
import com.example.order_management.model.OrderStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();

    List<Order> findByCustomerId(Integer id);
}
