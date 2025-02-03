package com.example.order_management.service;

import com.example.order_management.exception.OrderException;
import com.example.order_management.model.Order;
import com.example.order_management.model.OrderStatus;
import com.example.order_management.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        order.setCustomerId(1);
        order.setProducts(List.of("Produkt 1", "Produkt 2"));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);
        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getCustomerId());
        assertIterableEquals(List.of("Produkt 1", "Produkt 2"), createdOrder.getProducts());
    }

    @Test
    void testGetOrderById() {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        when(orderRepository.findById(eq(id))).thenReturn(Optional.of(order));

        Order retrievedOrder = orderService.getOrderById(id);
        assertNotNull(retrievedOrder);
        assertEquals(id, retrievedOrder.getId());
    }

    @Test
    void testGetOrderByIdNotFound() {
        Long id = 1L;
        when(orderRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.getOrderById(id));
    }

    @Test
    void testUpdateOrderStatus() {
        Long id = 1L;
        OrderStatus status = OrderStatus.W_TRAKCIE;
        Order order = new Order();
        order.setStatus(status);
        when(orderRepository.findById(eq(id))).thenReturn(Optional.of(order));

        Order updatedOrder = orderService.getOrderById(id);
        assertNotNull(updatedOrder);
        assertEquals(status, updatedOrder.getStatus());
    }

    @Test
    void testUpdateOrderStatusNotFound() {
        Long id = 1L;
        OrderStatus status = OrderStatus.W_TRAKCIE;

        when(orderRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.updateOrderStatus(id, status));
    }

    @Test
    void testDeleteOrder() {
        Long id = 1L;
        when(orderRepository.existsById(eq(id))).thenReturn(true);

        orderService.deleteOrder(id);
        verify(orderRepository, times(1)).deleteById(eq(id));
    }

    @Test
    void testDeleteOrderNotFound() {
        Long id = 1L;

        when(orderRepository.existsById(eq(id))).thenReturn(false);

        assertThrows(OrderException.class, () -> orderService.deleteOrder(id));
    }

    @Test
    void testGetOrdersByStatus() {
        OrderStatus status = OrderStatus.NOWE;
        List<Order> orders = List.of(new Order(), new Order());

        when(orderRepository.findByStatus(eq(status))).thenReturn(orders);

        List<Order> retrievedOrders = orderService.getOrdersByStatus(status);
        assertNotNull(retrievedOrders);
        assertEquals(2, retrievedOrders.size());
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> retrievedOrders = orderService.getAll();
        assertNotNull(retrievedOrders);
        assertEquals(2, retrievedOrders.size());
    }
}
