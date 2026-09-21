package com.cravedash.order.dto;

import java.util.List;

public class CreateOrderRequest {
    private Long restaurantId;
    private Long customerId;
    private List<OrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(Long restaurantId, Long customerId, List<OrderItemRequest> items) {
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.items = items;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
