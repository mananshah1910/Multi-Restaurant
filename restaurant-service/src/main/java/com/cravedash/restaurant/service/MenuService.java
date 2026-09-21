package com.cravedash.restaurant.service;

import com.cravedash.restaurant.entity.MenuItem;
import com.cravedash.restaurant.repository.MenuItemRepository;
import com.cravedash.restaurant.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    private void verifyRestaurantExists(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found with id: " + restaurantId);
        }
    }

    public MenuItem addMenuItem(Long restaurantId, MenuItem menuItem) {
        verifyRestaurantExists(restaurantId);
        if (menuItem.getName() == null || menuItem.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item name is required");
        }
        if (menuItem.getPrice() == null || menuItem.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid price is required");
        }
        menuItem.setRestaurantId(restaurantId);
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        verifyRestaurantExists(restaurantId);
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public MenuItem getMenuItem(Long restaurantId, Long itemId) {
        verifyRestaurantExists(restaurantId);
        return menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found with id: " + itemId));
    }

    public MenuItem updateMenuItem(Long restaurantId, Long itemId, MenuItem updated) {
        MenuItem existing = getMenuItem(restaurantId, itemId);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
        if (updated.getAvailability() != null) existing.setAvailability(updated.getAvailability());
        return menuItemRepository.save(existing);
    }

    public MenuItem updateAvailability(Long restaurantId, Long itemId, Boolean available) {
        MenuItem existing = getMenuItem(restaurantId, itemId);
        existing.setAvailability(available);
        return menuItemRepository.save(existing);
    }

    public void deleteMenuItem(Long restaurantId, Long itemId) {
        MenuItem existing = getMenuItem(restaurantId, itemId);
        menuItemRepository.delete(existing);
    }
}
