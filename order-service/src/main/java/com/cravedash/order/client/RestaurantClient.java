package com.cravedash.order.client;

import com.cravedash.order.dto.MenuItemDto;
import com.cravedash.order.dto.RestaurantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {

    @GetMapping("/restaurants/{id}")
    RestaurantDto getRestaurantById(@PathVariable("id") Long id);

    @GetMapping("/restaurants/{restaurantId}/menu/{itemId}")
    MenuItemDto getMenuItem(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId);
}
