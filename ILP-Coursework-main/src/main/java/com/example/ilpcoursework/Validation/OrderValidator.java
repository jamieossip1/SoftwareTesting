package com.example.ilpcoursework.Validation;

import com.example.ilpcoursework.Data.Order;
import com.example.ilpcoursework.Data.Pizza;
import com.example.ilpcoursework.RestClientData.MenuItem;
import com.example.ilpcoursework.RestClientData.Restaurant;
import org.springframework.cglib.core.Local;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class OrderValidator {

    public boolean menuValidate(Order order, Restaurant[] restaurants) {
        // Iterate over each pizza in the order
        for (Pizza orderedPizza : order.getPizzasInOrder()) {
            boolean pizzaFound = false;

            // Check if pizza name is null
            if (orderedPizza.getName() == null) {
                return false;  // Immediately return false if pizza name is null
            }

            // Check each restaurant's menu to find the pizza
            for (Restaurant restaurant : restaurants) {
                for (MenuItem menuItem : restaurant.getMenu()) {
                    if (menuItem.getName().equals(orderedPizza.getName())) {
                        pizzaFound = true;
                        break; // Stop searching once we find a match in any restaurant
                    }
                }
                if (pizzaFound) break; // Exit loop if pizza found in any restaurant
            }

            // If a pizza from the order isn't found in any restaurant's menu, log an error and return false
            if (!pizzaFound) {
                return false;
            }
        }

        // All pizzas in the order were found in at least one restaurant's menu
        return true;
    }

    public boolean maxPizzas(Order order) {
        return order.getPizzasInOrder().size() <= 4;
    }

    public boolean isExpiryDateValid(String creditCardExpiry) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        try {
            YearMonth expiryDate = YearMonth.parse(creditCardExpiry, formatter);
            YearMonth currentMonth = YearMonth.now();

            return expiryDate.isAfter(currentMonth) || expiryDate.equals(currentMonth);

        } catch (DateTimeException e) {
            return false;
        }
    }

    public boolean validateTotalPrice(Order order) {
        int calculatedTotal = order.getPizzasInOrder().stream().mapToInt(Pizza::getPriceInPence).sum();
        return calculatedTotal+100 == order.getPriceTotalInPence();
    }

    public boolean diffRestaurants(Order order, Restaurant[] restaurants) {
        // This map will track the restaurants from which each pizza is found

        String pizzaRestaurant = null;
        // Iterate over each pizza in the order
        for (Pizza orderedPizza : order.getPizzasInOrder()) {
            boolean pizzaFound = false;


            // Check each restaurant's menu to find the pizza
            for (Restaurant restaurant : restaurants) {
                for (MenuItem menuItem : restaurant.getMenu()) {
                    if (menuItem.getName() != null && menuItem.getName().equals(orderedPizza.getName())) {
                        pizzaFound = true;

                        // Track which restaurant the pizza is from
                        if (pizzaRestaurant == null) {
                            pizzaRestaurant = restaurant.getName();  // Store the first restaurant
                        } else if (!pizzaRestaurant.equals(restaurant.getName())) {
                            // If the pizza is found in a different restaurant, it's invalid
                            return false;  // Return false if pizza is from multiple restaurants
                        }

                        break;  // Stop searching once we find a match
                    }
                }
                if (pizzaFound) break;  // Exit loop if pizza found in any restaurant
            }

        }

        // If all pizzas were found in only one restaurant, the order is valid
        return true;
    }


    public boolean restaurantStatus(Order order, Restaurant[] restaurants) {
        Restaurant rest = findRestaurant(order.getPizzasInOrder().getFirst().getName(),restaurants);

        String dayOfWeek = convert(order.getOrderDate()).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        return rest.getOpeningDays().contains(dayOfWeek);
    }

    public LocalDate convert(String orderDateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(orderDateString, formatter);
        } catch (DateTimeException e) {
            return null;
        }
    }


    public boolean priceCheck(Order order, Restaurant[] restaurants) {
        for (Pizza orderedPizza : order.getPizzasInOrder()) {
            boolean pizzaFound = false;
            Restaurant rest = findRestaurant(order.getPizzasInOrder().getFirst().getName(), restaurants);
            for (MenuItem menuItem : rest.getMenu()) {
                if (menuItem.getName().equals(orderedPizza.getName())) {
                    pizzaFound = true;
                    if (orderedPizza.getPriceInPence() != menuItem.getPriceInPence()) {
                        return false;
                    }
                }
                if (pizzaFound) {
                    break;
                }

            }

        }
        return true;
    }

    public Restaurant findRestaurant(String pizzaName, Restaurant[] restaurants) {
        for (Restaurant restaurant : restaurants) {
            for (MenuItem menuItem : restaurant.getMenu()) {
                if (menuItem.getName().equals(pizzaName)) {
                    return restaurant;
                }
            }
        }
        return null;
    }

    public boolean emptyOrder(Order order) {
        return order.getPizzasInOrder() != null && !order.getPizzasInOrder().isEmpty();
    }

}
