package com.example.order_management;

import com.example.order_management.model.Order;
import com.example.order_management.model.OrderStatus;
import com.example.order_management.repository.OrderRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class OrderManagementApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(OrderManagementApplication.class, args);
		OrderRepository orderRepository = context.getBean(OrderRepository.class);
		Order order1 = new Order();
		order1.setProducts(Arrays.asList("Product A", "Product B"));
		order1.setCustomerId(12);
		order1.setCreatedAt(LocalDateTime.now());
		order1.setStatus(OrderStatus.NOWE);

		orderRepository.save(order1);
	}
}
