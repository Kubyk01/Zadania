    package com.example.order_management.service;

    import com.example.order_management.exception.OrderException;
    import com.example.order_management.model.Order;
    import com.example.order_management.model.OrderStatus;
    import com.example.order_management.repository.OrderRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.List;

    @Service
    public class OrderService {

        @Autowired
        private OrderRepository orderRepository;

        @Transactional
        public Order createOrder(Order order) {
            if (order.getCustomerId() == null || order.getProducts().isEmpty()) {
                throw new OrderException("Klient i produkty są wymagane.");
            }
            order.setCreatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        }

        @Transactional(readOnly = true)
        public Order getOrderById(Long id) {
            return orderRepository.findById(id).orElseThrow(() -> new OrderException("Zamówienie o ID " + id + " nie istnieje."));
        }

        @Transactional
        public Order updateOrderStatus(Long id, OrderStatus status) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new OrderException("Zamówienie o ID " + id + " nie istnieje."));
            order.setStatus(status);
            return orderRepository.save(order);
        }

        @Transactional(readOnly = true)
        public void deleteOrder(Long id) {
            if (!orderRepository.existsById(id)) {
                throw new OrderException("Zamówienie o ID " + id + " nie istnieje.");
            }
            orderRepository.deleteById(id);
        }

        @Transactional(readOnly = true)
        public List<Order> getOrdersByStatus(OrderStatus status) {
            return orderRepository.findByStatus(status);
        }

        @Transactional(readOnly = true)
        public List<Order> getOrdersByCustomerId(Integer id) {
            return orderRepository.findByCustomerId(id);
        }

        @Transactional(readOnly = true)
        public List<Order> getAll() {
            return orderRepository.findAll();
        }
    }
