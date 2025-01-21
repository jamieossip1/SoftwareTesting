package com.example.ilpcoursework;

import com.example.ilpcoursework.Codes.OrderStatus;
import com.example.ilpcoursework.Codes.OrderValidationCode;
import com.example.ilpcoursework.Controller.BasicController;
import com.example.ilpcoursework.Controller.DronePathFinding;
import com.example.ilpcoursework.Data.*;
import com.example.ilpcoursework.RestClientData.CentralArea;
import com.example.ilpcoursework.RestClientData.MenuItem;
import com.example.ilpcoursework.RestClientData.Restaurant;
import com.example.ilpcoursework.RestClientData.noFlyZones;
import com.example.ilpcoursework.Validation.OrderValidator;
import com.example.ilpcoursework.client.RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CW2Tests {

    @Autowired
    private BasicController basicController;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @MockBean
    private BasicController mockBasicController;

    @Test
    void contextLoads() throws Exception {
        assertThat(basicController).isNotNull();
    }


    //Example of mocking for the undefined pizza example, where i create fake restaurant and pizza data
    @Test
    void menuValidate_withUndefinedPizza_returnsFalse() {
        // Arrange: Mock restaurant menus
        MenuItem validMenuItem1 = new MenuItem();
        validMenuItem1.setName("R6: Sucuk delight");
        validMenuItem1.setPriceInPence(1400);
        MenuItem validMenuItem2 = new MenuItem();
        validMenuItem2.setName("R6: Dreams of Syria");
        validMenuItem2.setPriceInPence(900);
        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setName("R6");
        List<MenuItem> validMenuItems = new ArrayList<>();
        validMenuItems.add(validMenuItem1);
        validMenuItems.add(validMenuItem2);
        mockRestaurant.setMenu(validMenuItems);
        mockRestaurant.setLocation(new LngLat(-3.188272079973018, 55.94386018523326));
        List<String> openingDays = new ArrayList<>();
        openingDays.add("Saturday");
        openingDays.add("Sunday");
        openingDays.add("Monday");
        openingDays.add("Tuesday");
        openingDays.add("Wednesday");
        openingDays.add("Thursday");
        openingDays.add("Friday");
        mockRestaurant.setOpeningDays(openingDays);

        // Mock restaurants array
        Restaurant[] mockRestaurants = new Restaurant[]{mockRestaurant};

        // Create an order with a pizza not in the menu
        Pizza undefinedPizza = new Pizza("Unknown Delight", 1400);
        Order orderWithUndefinedPizza = new Order("09C7D13F", "2025-01-11", 1400, List.of(undefinedPizza), null);


        OrderValidator orderValidator = new OrderValidator();
        // Act: Call the method with the mocked data
        boolean isValid = orderValidator.menuValidate(orderWithUndefinedPizza, mockRestaurants);

        // Assert: The validation should fail as the pizza is not in the menu
        assertThat(isValid).isFalse();
    }



    //Mocking for a true case
    @Test
    void menuValidate_withUndefinedPizza_returnsTrue() {
        // Arrange: Mock restaurant menus
        MenuItem validMenuItem1 = new MenuItem();
        validMenuItem1.setName("R6: Sucuk delight");
        validMenuItem1.setPriceInPence(1400);
        MenuItem validMenuItem2 = new MenuItem();
        validMenuItem2.setName("R6: Dreams of Syria");
        validMenuItem2.setPriceInPence(900);
        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setName("R6");
        List<MenuItem> validMenuItems = new ArrayList<>();
        validMenuItems.add(validMenuItem1);
        validMenuItems.add(validMenuItem2);
        mockRestaurant.setMenu(validMenuItems);
        mockRestaurant.setLocation(new LngLat(-3.188272079973018, 55.94386018523326));
        List<String> openingDays = new ArrayList<>();
        openingDays.add("Saturday");
        openingDays.add("Sunday");
        openingDays.add("Monday");
        openingDays.add("Tuesday");
        openingDays.add("Wednesday");
        openingDays.add("Thursday");
        openingDays.add("Friday");
        mockRestaurant.setOpeningDays(openingDays);

        // Mock restaurants array
        Restaurant[] mockRestaurants = new Restaurant[]{mockRestaurant};

        // Create an order with a pizza not in the menu
        Pizza undefinedPizza = new Pizza("R6: Sucuk delight", 1400);
        Order orderWithUndefinedPizza = new Order("09C7D13F", "2025-01-11", 1500, List.of(undefinedPizza), null);


        OrderValidator orderValidator = new OrderValidator();
        // Act: Call the method with the mocked data
        boolean isValid = orderValidator.menuValidate(orderWithUndefinedPizza, mockRestaurants);

        // Assert: The validation should fail as the pizza is not in the menu
        assertThat(isValid).isTrue();
    }






//Unit testing for the validation
    @Test
    void validateOrder_withValidOrder_returnsValidStatus() {
        // Arrange: Create test data for a valid order
        List<Pizza> pizzas = List.of(
                new Pizza("R6: Sucuk delight", 1400),
                new Pizza("R6: Dreams of Syria", 900)
        );

        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");

        Order validOrder = new Order("09C7D13F", "2025-01-11", 2400, pizzas, creditCard);

        // Act: Make the request
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                validOrder,
                OrderValidationResult.class
        );

        // Assert: Check the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.VALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.NO_ERROR);
    }

    @Test
    void validateOrder_withInvalidCreditCardNumber_returnsCardNumberInvalidStatus() {
        // Arrange: Create an order with an invalid credit card number
        List<Pizza> pizzas = List.of(
                new Pizza("R6: Sucuk delight", 1400),
                new Pizza("R6: Dreams of Syria", 900)
        );

        CreditCardInformation invalidCreditCard = new CreditCardInformation("12345", "02/25", "123");

        Order invalidOrder = new Order("09C7D13F", "2025-01-11", 2400, pizzas, invalidCreditCard);

        // Act: Make the request
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                invalidOrder,
                OrderValidationResult.class
        );

        // Assert: Check the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.CARD_NUMBER_INVALID);
    }

    @Test
    void validateOrder_withEmptyOrder_returnsEmptyOrderStatus() {
        // Arrange: Create an empty order
        List<Pizza> emptyPizzaList = List.of();
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");

        Order emptyOrder = new Order("09C7D13F", "2025-01-11", 0, emptyPizzaList, creditCard);

        // Act: Make the request
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                emptyOrder,
                OrderValidationResult.class
        );

        // Assert: Check the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.EMPTY_ORDER);
    }

    @Test
    void validateOrder_invalidCardNumber_returnsCardNumberInvalidStatus() {
        List<Pizza> pizzas = List.of(new Pizza("R6: Sucuk delight", 1500));
        CreditCardInformation invalidCard = new CreditCardInformation("12345", "02/25", "123");

        Order invalidOrder = new Order("09C7D13F", "2025-01-11", 1400, pizzas, invalidCard);

        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                invalidOrder,
                OrderValidationResult.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.CARD_NUMBER_INVALID);
    }

    @Test
    void validateOrder_expiredCard_returnsExpiryDateInvalidStatus() {
        List<Pizza> pizzas = List.of(new Pizza("R6: Sucuk delight", 1400));
        CreditCardInformation expiredCard = new CreditCardInformation("1234567812345678", "01/20", "123");

        Order invalidOrder = new Order("09C7D13F", "2025-01-11", 1500, pizzas, expiredCard);

        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                invalidOrder,
                OrderValidationResult.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.EXPIRY_DATE_INVALID);
    }

    @Test
    void validateOrder_invalidCvv_returnsCvvInvalidStatus() {
        List<Pizza> pizzas = List.of(new Pizza("R6: Sucuk delight", 1400));
        CreditCardInformation invalidCvvCard = new CreditCardInformation("1234567812345678", "02/25", "12");

        Order invalidOrder = new Order("09C7D13F", "2025-01-11", 1500, pizzas, invalidCvvCard);

        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                invalidOrder,
                OrderValidationResult.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.CVV_INVALID);
    }

    @Test
    void validateOrder_incorrectTotal_returnsTotalIncorrectStatus() {
        List<Pizza> pizzas = List.of(
                new Pizza("R6: Sucuk delight", 1400),
                new Pizza("R6: Dreams of Syria", 900)
        );
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");

        Order incorrectTotalOrder = new Order("09C7D13F", "2025-01-11", 2500, pizzas, creditCard);

        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                incorrectTotalOrder,
                OrderValidationResult.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.TOTAL_INCORRECT);
    }

    @Test
    void validateOrder_undefinedPizza_returnsPizzaNotDefinedStatus() {
        List<Pizza> pizzas = List.of(new Pizza("Unknown Delight", 1400)); // Pizza not on menu
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");

        Order undefinedPizzaOrder = new Order("09C7D13F", "2025-01-11", 1500, pizzas, creditCard);

        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                undefinedPizzaOrder,
                OrderValidationResult.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.PIZZA_NOT_DEFINED);
    }

    @Test
    void validateOrder_exceedsMaxPizzas_returnsMaxPizzaCountExceededStatus() {
        List<Pizza> pizzas = List.of(
                new Pizza("R6: Sucuk delight", 1400),
                new Pizza("R6: Dreams of Syria", 900),
                new Pizza("R6: Dreams of Syria", 900),
                new Pizza("R6: Dreams of Syria", 900),
                new Pizza("R6: Dreams of Syria", 900)
        );
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");

        Order tooManyPizzasOrder = new Order("09C7D13F", "2025-01-11", 5100, pizzas, creditCard);

        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                tooManyPizzasOrder,
                OrderValidationResult.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
    }

    @Test
    void validateOrder_restaurantClosed_returnsRestaurantClosedStatus() {
        // Arrange: Order on a Wednesday (2025-01-15 is a Wednesday)
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 1000));
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order orderOnWednesday = new Order("09C7D13F", "2025-01-15", 1100, pizzas, creditCard);

        // Act: Send the order for validation
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                orderOnWednesday,
                OrderValidationResult.class
        );

        // Assert: Expect RESTAURANT_CLOSED status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.RESTAURANT_CLOSED);
    }

    @Test
    void validateOrder_priceinvalid_returnsPrice_InvalidStatus() {
        // Arrange: Order on a Wednesday (2025-01-15 is a Wednesday)
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 900));
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order orderOnWednesday = new Order("09C7D13F", "2025-01-11", 1000, pizzas, creditCard);

        // Act: Send the order for validation
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                orderOnWednesday,
                OrderValidationResult.class
        );

        // Assert: Expect RESTAURANT_CLOSED status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.PRICE_FOR_PIZZA_INVALID);
    }

    @Test
    void validateOrder_multipleRestaurants_returnsMultipleRestaurantsStatus() {
        // Arrange: Order on a Wednesday (2025-01-15 is a Wednesday)
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 1000),new Pizza("R6: Sucuk delight", 1400) );
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order orderOnWednesday = new Order("09C7D13F", "2025-01-11", 2500, pizzas, creditCard);

        // Act: Send the order for validation
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/validateOrder",
                orderOnWednesday,
                OrderValidationResult.class
        );

        // Assert: Expect RESTAURANT_CLOSED status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderStatus()).isEqualTo(OrderStatus.INVALID);
        assertThat(response.getBody().getOrderValidationCode()).isEqualTo(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
    }

    //Test cases for path finding algorithm
    @Test
    void validatePath_return_400_invalidOrder() {
        // Arrange: Order on a Wednesday (2025-01-15 is a Wednesday)
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 1000),new Pizza("R6: Sucuk delight", 1400) );
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order orderOnWednesday = new Order("09C7D13F", "2025-01-11", 2500, pizzas, creditCard);

        // Act: Send the order for validation
        ResponseEntity<OrderValidationResult> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/calcDeliveryPath",
                orderOnWednesday,
                OrderValidationResult.class
        );

        // Assert: Expect RESTAURANT_CLOSED status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void testRestaurantInsideCentral(){
        LngLat rest = new LngLat(-3.188272079973018, 55.94386018523326);
        DronePathFinding dpf = new DronePathFinding();
        RestClient rc = new RestClient();
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 1000) );
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order orderOnWednesday = new Order("09C7D13F", "2025-01-11", 1100, pizzas, creditCard);

        List<LngLat> path = dpf.pathFinder(rest,RestClient.getAllNoFlyZones(),RestClient.getCentralArea(),orderOnWednesday);
    }

    //Test to ensure all points on the path are not in noFlyZones
    @Test
    void validatePath_avoidNoFlyZones() throws JsonProcessingException {
        // Arrange: Setup restaurant location, order, and other necessary objects
        LngLat rest = new LngLat(-3.188272079973018, 55.94386018523326);
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 1000));
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order orderOnWednesday = new Order("09C7D13F", "2025-01-11", 1100, pizzas, creditCard);

        // Act: Send the order for path calculation
        ResponseEntity<Object> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/calcDeliveryPath",
                orderOnWednesday,
                Object.class // Retrieve as raw Object for manual deserialization
        );

        // Assert: Check response status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Manually deserialize the response body to a List<LngLat>
        ObjectMapper objectMapper = new ObjectMapper();
        List<LngLat> path = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});

        // Validate the path points
        assertNotNull(path, "Path should not be null");
        BasicController BC = new BasicController();
        noFlyZones [] noFlyZones = RestClient.getAllNoFlyZones();
        for (LngLat point : path) {
            for (noFlyZones noFlyZone : noFlyZones) {
                assertFalse(
                        BC.checkRegion(new IsInRegionRequest(point, new Region("NoFlyZone", noFlyZone.getVertices()))),
                        "Path point " + point + " lies within a no-fly zone!"
                );
            }
        }
    }


    //Check to make sure the path never leaves the centralArea after entering it
    @Test
    void validatePath_staysInCentralRegion() {
        // Arrange: Setup restaurant location, order, and other necessary objects
        LngLat rest = new LngLat(-3.188272079973018, 55.94386018523326); // Example restaurant location
        List<Pizza> pizzas = List.of(new Pizza("R1: Margarita", 1000));
        CreditCardInformation creditCard = new CreditCardInformation("1234567812345678", "02/25", "123");
        Order order = new Order("09C7D13F", "2025-01-11", 1100, pizzas, creditCard); // Example order on a valid day

        // Act: Calculate the delivery path
        ResponseEntity<Object> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/calcDeliveryPath",
                order,
                Object.class // Retrieve as raw Object for manual deserialization
        );

        // Assert: Check response status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Manually deserialize the response body to a List<LngLat>
        ObjectMapper objectMapper = new ObjectMapper();
        List<LngLat> path = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});


        BasicController BC = new BasicController();

        // Assert: Validate the response and ensure the path stays within the central region


        assertNotNull(path, "Path should not be null");
        assertFalse(path.isEmpty(), "Path should not be empty");

        CentralArea centralRegion = RestClient.getCentralArea(); // Central region polygon

        boolean inCentral = false; // Tracks whether we are inside the central region
        for (LngLat point : path) {
            boolean isInsideCentral = BC.checkRegion(
                    new IsInRegionRequest(point, new Region("Central", centralRegion.getVertices()))
            );

            if (isInsideCentral) {
                inCentral = true; // Mark as entered the central region
            }

            if (inCentral) {
                // Once inside the central region, ensure the point does not exit
                assertTrue(
                        isInsideCentral,
                        "Path point " + point + " exits the central region after entering it!"
                );
            }
        }

        // Assert: Final validation that the path at least partially entered the central region
        assertTrue(inCentral, "Path never entered the central region!");
    }

























}