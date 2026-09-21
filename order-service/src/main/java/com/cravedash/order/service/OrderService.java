package com.cravedash.order.service;

import com.cravedash.order.client.PaymentClient;
import com.cravedash.order.client.RestaurantClient;
import com.cravedash.order.dto.*;
import com.cravedash.order.entity.Order;
import com.cravedash.order.entity.OrderItem;
import com.cravedash.order.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository, RestaurantClient restaurantClient, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.restaurantClient = restaurantClient;
        this.paymentClient = paymentClient;
    }

    public Order createOrder(CreateOrderRequest request) {
        if (request.getRestaurantId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant ID is required");
        }
        if (request.getCustomerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer ID is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        // Step 1: Restaurant verification
        RestaurantDto restaurant;
        try {
            restaurant = restaurantClient.getRestaurantById(request.getRestaurantId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found with id: " + request.getRestaurantId());
        }

        if (restaurant == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found with id: " + request.getRestaurantId());
        }

        // Step 2 & 3: Availability & Price Verification & Total calculation
        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity for menu item ID: " + itemReq.getMenuItemId());
            }

            MenuItemDto menuItem;
            try {
                menuItem = restaurantClient.getMenuItem(request.getRestaurantId(), itemReq.getMenuItemId());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item not found with id: " + itemReq.getMenuItemId());
            }

            if (menuItem == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item not found with id: " + itemReq.getMenuItemId());
            }

            if (!menuItem.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item is currently unavailable: " + menuItem.getName());
            }

            double itemPrice = menuItem.getPrice();
            totalAmount += itemPrice * itemReq.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(itemPrice);
            orderItems.add(orderItem);
        }

        // Step 4: Create Order Record
        Order order = new Order();
        order.setRestaurantId(request.getRestaurantId());
        order.setCustomerId(request.getCustomerId());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        for (OrderItem item : orderItems) {
            order.getItems().add(item);
        }

        Order savedOrder = orderRepository.save(order);

        // Step 5: Initiate Payment Transaction
        try {
            paymentClient.createPayment(new PaymentRequest(savedOrder.getId(), totalAmount, "PENDING"));
        } catch (Exception e) {
            System.err.println("Warning: Payment creation failed for order " + savedOrder.getId() + ": " + e.getMessage());
        }

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
    }

    public OrderStatusResponse getOrderStatus(Long id) {
        Order order = getOrderById(id);
        return new OrderStatusResponse(order.getId(), order.getStatus());
    }

    public Order updateOrder(Long id, Order updated) {
        Order existing = getOrderById(id);
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getTotalAmount() != null) existing.setTotalAmount(updated.getTotalAmount());
        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        Order existing = getOrderById(id);
        orderRepository.delete(existing);
    }
}
