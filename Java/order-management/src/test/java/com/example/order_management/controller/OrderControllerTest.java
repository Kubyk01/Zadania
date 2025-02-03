package com.example.order_management.controller;

import static com.example.order_management.model.OrderStatus.NOWE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.order_management.exception.OrderException;
import com.example.order_management.model.Order;
import com.example.order_management.model.OrderStatus;
import com.example.order_management.service.OrderService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrderSuccess() throws Exception {
        Order order = new Order();
        order.setCustomerId(1);
        order.setProducts(Arrays.asList("Product 1", "Product 2"));
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .content("{\"customerId\": 1, \"products\":[\"Product 1\", \"Product 2\"]}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.products[0]").value("Product 1"))
                .andExpect(jsonPath("$.products[1]").value("Product 2"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    public void testCreateOrderFailure() throws Exception {
        assertThrows(ServletException.class, () -> mockMvc.perform(post("/orders")
                .content("{\"customerId\": null, \"products\": null}")
                .contentType(MediaType.APPLICATION_JSON)));
    }

    @Test
    public void testGetOrderByIdSuccess() throws Exception {
        mockMvc.perform(post("/orders")
                .content("{\"customerId\": 1, \"products\":[\"Product 1\", \"Product 2\"]}")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.products").exists())
                .andExpect(jsonPath("$.customerId").exists());
    }

    @Test
    public void testGetOrderByIdFailure() throws Exception {
        assertThrows(ServletException.class, () -> mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound()));

    }

    @Test
    public void testUpdateOrderStatusSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        when(orderService.updateOrderStatus(1L, OrderStatus.W_TRAKCIE)).thenReturn(order);

        mockMvc.perform(put("/orders/1/status")
                        .content("{\"status\":\"W_TRAKCIE\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("W_TRAKCIE"));
    }

    @Test
    public void testDeleteOrderSuccess() throws Exception {
        mockMvc.perform(post("/orders")
                .content("{\"customerId\": 1, \"products\":[\"Product 1\", \"Product 2\"]}")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetOrdersByStatusSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        List<Order> orders = Arrays.asList(order);

        when(orderService.getOrdersByStatus(NOWE)).thenReturn(orders);

        mockMvc.perform(get("/orders/status/NOWE"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOrdersByCustomerIdSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        List<Order> orders = Arrays.asList(order);

        when(orderService.getOrdersByCustomerId(1)).thenReturn(orders);

        mockMvc.perform(post("/orders")
                .content("{\"customerId\": 1, \"products\":[\"Product 1\", \"Product 2\"]}")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/orders/customerId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void testGetAllOrdersSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        List<Order> orders = Arrays.asList(order);

        when(orderService.getAll()).thenReturn(orders);

        mockMvc.perform(get("/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}