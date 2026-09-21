package com.cravedash.restaurant;

import com.cravedash.restaurant.entity.MenuItem;
import com.cravedash.restaurant.entity.Restaurant;
import com.cravedash.restaurant.repository.MenuItemRepository;
import com.cravedash.restaurant.repository.RestaurantRepository;
import com.cravedash.restaurant.service.MenuService;
import com.cravedash.restaurant.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @InjectMocks
    private MenuService menuService;

    private Restaurant restaurant;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(1L, "Pizza Palace", "123 Main St", "555-1234", "ACTIVE");
        menuItem = new MenuItem(10L, 1L, "Margherita Pizza", "Cheesy goodness", 250.0, true);
    }

    @Test
    void createRestaurant_Success() {
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        Restaurant created = restaurantService.createRestaurant(restaurant);

        assertNotNull(created);
        assertEquals("Pizza Palace", created.getName());
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    void getRestaurantById_NotFound_ThrowsException() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> restaurantService.getRestaurantById(999L));
    }

    @Test
    void updateMenuItemAvailability_Success() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(menuItemRepository.findByIdAndRestaurantId(10L, 1L)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem updated = menuService.updateAvailability(1L, 10L, false);

        assertNotNull(updated);
        assertFalse(updated.getAvailability());
        assertFalse(updated.isAvailable());
    }

    @Test
    void getMenuItem_NotFound_ThrowsException() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(menuItemRepository.findByIdAndRestaurantId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> menuService.getMenuItem(1L, 999L));
    }
}
