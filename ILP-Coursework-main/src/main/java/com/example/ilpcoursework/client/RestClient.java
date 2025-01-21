package com.example.ilpcoursework.client;
import com.example.ilpcoursework.RestClientData.CentralArea;
import com.example.ilpcoursework.RestClientData.Restaurant;
import com.example.ilpcoursework.RestClientData.noFlyZones;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class RestClient {
private static final String REST_URL = "https://ilp-rest-2024.azurewebsites.net/restaurants";
private static final ObjectMapper mapper;
private static final String FLY_URL = "https://ilp-rest-2024.azurewebsites.net/noFlyZones";
private static final String CENTRAL_URL = "https://ilp-rest-2024.azurewebsites.net/centralArea";


    // Static initializer block to configure the mapper
    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }
//Get Restaurants from the REST service

    public static Restaurant[] getAllRestaurants() {
        try {
            return mapper.readValue(new URL(REST_URL), Restaurant[].class);
        } catch (Exception e) {
            System.err.println("Error getting restaurants: " + e.getMessage());
            e.printStackTrace();
            return new Restaurant[]{};
        }
    }

    public static noFlyZones[] getAllNoFlyZones(){
        try{
            return mapper.readValue(new URL(FLY_URL),noFlyZones[].class);
        } catch (Exception e) {
            System.err.println("Error getting Fly Zones: " +e.getMessage());
            e.printStackTrace();
            return new noFlyZones[] {};
        }


        }

    public static CentralArea getCentralArea(){
        try{
            return mapper.readValue(new URL(CENTRAL_URL), CentralArea.class);

        }catch (Exception e) {
            System.err.println("Error getting Central Area: " +e.getMessage());
            e.printStackTrace();
            return new CentralArea();
        }
    }

}
