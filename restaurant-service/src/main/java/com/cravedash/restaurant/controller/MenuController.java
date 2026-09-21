package com.cravedash.restaurant.controller;

import com.cravedash.restaurant.entity.MenuItem;
import com.cravedash.restaurant.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants/{restaurantId}/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItem menuItem) {
        return new ResponseEntity<>(menuService.addMenuItem(restaurantId, menuItem), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenuByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenuByRestaurant(restaurantId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long restaurantId, @PathVariable Long itemId) {
        return ResponseEntity.ok(menuService.getMenuItem(restaurantId, itemId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long restaurantId, @PathVariable Long itemId, @RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(menuService.updateMenuItem(restaurantId, itemId, menuItem));
    }

    @PutMapping("/{itemId}/availability")
    public ResponseEntity<MenuItem> updateAvailability(@PathVariable Long restaurantId, @PathVariable Long itemId, @RequestParam Boolean available) {
        return ResponseEntity.ok(menuService.updateAvailability(restaurantId, itemId, available));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long restaurantId, @PathVariable Long itemId) {
        menuService.deleteMenuItem(restaurantId, itemId);
        return ResponseEntity.noContent().build();
    }
}
