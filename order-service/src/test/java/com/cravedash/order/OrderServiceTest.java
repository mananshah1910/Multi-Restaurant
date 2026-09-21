package com.cravedash.order;

import com.cravedash.order.client.PaymentClient;
import com.cravedash.order.client.RestaurantClient;
import com.cravedash.order.dto.*;
import com.cravedash.order.entity.Order;
import com.cravedash.order.repository.OrderRepository;
import com.cravedash.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantClient restaurantClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderService orderService;

    private RestaurantDto restaurantDto;
    private MenuItemDto item1;
    private MenuItemDto item2;

    @BeforeEach
    void setUp() {
        restaurantDto = new RestaurantDto(1L, "Burger Joint", "Downtown", "555-9999", "ACTIVE");
        item1 = new MenuItemDto(10L, 1L, "Burger", "Juicy beef", 200.0, true);
        item2 = new MenuItemDto(11L, 1L, "Fries", "Crispy fries", 100.0, true);
    }

    @Test
    void createOrder_MultiItem_Success() {
        CreateOrderRequest request = new CreateOrderRequest(
                1L, 101L,
                List.of(new OrderItemRequest(10L, 2), new OrderItemRequest(11L, 1))
        );

        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurantDto);
        when(restaurantClient.getMenuItem(1L, 10L)).thenReturn(item1);
        when(restaurantClient.getMenuItem(1L, 11L)).thenReturn(item2);

        Order savedOrder = new Order(1001L, 1L, 101L, 500.0, "PENDING");
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(500.0, result.getTotalAmount());
        assertEquals("PENDING", result.getStatus());
        verify(paymentClient, times(1)).createPayment(any(PaymentRequest.class));
    }

    @Test
    void createOrder_UnavailableItem_ThrowsException() {
        MenuItemDto unavailableItem = new MenuItemDto(10L, 1L, "Burger", "Juicy beef", 200.0, false);
        CreateOrderRequest request = new CreateOrderRequest(
                1L, 101L,
                List.of(new OrderItemRequest(10L, 1))
        );

        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurantDto);
        when(restaurantClient.getMenuItem(1L, 10L)).thenReturn(unavailableItem);

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_InvalidQuantity_ThrowsException() {
        CreateOrderRequest request = new CreateOrderRequest(
                1L, 101L,
                List.of(new OrderItemRequest(10L, 0))
        );

        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurantDto);

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
