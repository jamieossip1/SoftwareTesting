package com.example.ilpcoursework;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ilpcoursework.RestClientData.CentralArea;
import com.example.ilpcoursework.RestClientData.Restaurant;
import com.example.ilpcoursework.RestClientData.noFlyZones;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

public class IntegrationTests {


    private static final String REST_URL ="https://ilp-rest-2024.azurewebsites.net/restaurants";
    private static final String FLY_URL = "https://ilp-rest-2024.azurewebsites.net/noFlyZones";
    private static final String CENTRAL_URL = "https://ilp-rest-2024.azurewebsites.net/centralArea";

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGetAllRestaurants() {
        Restaurant[] restaurants = null;

        try {
            restaurants = mapper.readValue(new URL(REST_URL), Restaurant[].class);
        } catch (Exception e) {
            fail("Exception occurred while fetching restaurants: " + e.getMessage());
        }

        assertNotNull(restaurants, "Restaurants array should not be null");
        assertTrue(restaurants.length > 0, "Restaurants array should not be empty");
    }

    @Test
    public void testGetAllNoFlyZones() {
        noFlyZones[] noFlyZones = null;

        try {
            noFlyZones = mapper.readValue(new URL(FLY_URL), noFlyZones[].class);
        } catch (Exception e) {
            fail("Exception occurred while fetching no-fly zones: " + e.getMessage());
        }

        assertNotNull(noFlyZones, "NoFlyZones array should not be null");
        assertTrue(noFlyZones.length > 0, "NoFlyZones array should not be empty");
    }

    @Test
    public void testGetCentralArea() {
        CentralArea centralArea = null;

        try {
            centralArea = mapper.readValue(new URL(CENTRAL_URL), CentralArea.class);
        } catch (Exception e) {
            fail("Exception occurred while fetching central area: " + e.getMessage());
        }

        assertNotNull(centralArea, "CentralArea object should not be null");
        // Add specific validations based on the CentralArea properties if needed
    }
}


