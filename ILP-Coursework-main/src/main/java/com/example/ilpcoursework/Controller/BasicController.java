package com.example.ilpcoursework.Controller;

import com.example.ilpcoursework.Codes.OrderStatus;
import com.example.ilpcoursework.Codes.OrderValidationCode;
import com.example.ilpcoursework.Data.*;
import com.example.ilpcoursework.RestClientData.CentralArea;
import com.example.ilpcoursework.RestClientData.Restaurant;
import com.example.ilpcoursework.Validation.OrderValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.ilpcoursework.client.RestClient;

import javax.naming.spi.ResolveResult;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class BasicController {

    @GetMapping("/isAlive")
    public boolean isAlive() {
        return true;
    }

    @GetMapping("/uuid")
    public String getStudentID() {
        return "s2415872";
    }

    @PostMapping("/distanceTo")
    public double getDistanceTo(@RequestBody LngLatPairRequest request) {
        try {
            validateInput(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        //Extract the positions
        LngLat position1 = request.getPosition1();
        LngLat position2 = request.getPosition2();

        //Calculate distance
        double distance = Math.sqrt(Math.pow(position2.getLng() - position1.getLng(), 2) + Math.pow(position2.getLat() - position1.getLat(), 2));
        return distance;
    }


    //Validation for distance method
    private void validateInput(LngLatPairRequest request) {
        if (request.getPosition1() == null || request.getPosition2() == null) {
            throw new IllegalArgumentException("Requests or positions cannot be null");
        }
        LngLat position1 = request.getPosition1();
        LngLat position2 = request.getPosition2();

        if (position1.getLng() == null || position1.getLat() == null ||
                position2.getLng() == null || position2.getLat() == null) {
            throw new IllegalArgumentException("Longitude and latitude must be provided for both positions.");
        }

        //Check if the coordinates are valid (not null and within range)
        if (!isValidCoordinate(position1.getLat(), position1.getLng()) ||
                !isValidCoordinate(position2.getLat(), position2.getLng())) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }

    //Validation checking a co-ordinate is valid
    private boolean isValidCoordinate(Double lat, Double lng) {
        return lat != null && lng != null && lng >= -180 && lng <= 180 && lat >= -90 && lat <= 90;
    }

    @PostMapping("/isCloseTo")
    public boolean checkCloseness(@RequestBody LngLatPairRequest request) {
        double index = 0.00015;//change back
        return getDistanceTo(request) < index;
    }

    @PostMapping("/nextPosition")
    public LngLat getNextPosition(@RequestBody NextPositionRequest nextRequest) {
        double distance = 0.00015; // Fixed movement distance in degrees

        try {
            validateNextPos(nextRequest);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        // Extract starting coordinates and angle
        double startLat = nextRequest.getStart().getLat();
        double startLng = nextRequest.getStart().getLng();
        double angle = nextRequest.getAngle();

        // Convert angle to radians
        double radians = Math.toRadians(angle);



        // Calculate change in latitude and longitude
        double deltaLat = distance * Math.sin(radians);
        double deltaLng = distance * Math.cos(radians);

        // Compute new latitude and longitude
        double newLat = startLat + deltaLat;
        double newLng = startLng + deltaLng;

        // Return the new position as an LngLat object
        return new LngLat(newLng, newLat);
    }

    //Validation for next position
    public void validateNextPos(NextPositionRequest nextRequest) {
        if (nextRequest.getStart() == null || nextRequest.getAngle() == null || nextRequest == null) {
            throw new IllegalArgumentException("Angle or co-ordinates cannot be null");
        }
        LngLat start = nextRequest.getStart();
        if (!isValidCoordinate(start.getLat(), start.getLng())) {
            throw new IllegalArgumentException("Longitude and latitude must not be null and must be valid");
        }
        if (nextRequest.getAngle() < 0 || /*(nextRequest.getAngle() % 22.5) != 0 ||*/ nextRequest.getAngle() > 360 /*|| nextRequest.getAngle() == -0.0*/) {
            throw new IllegalArgumentException("Angle must be 0 to 360");
        }


    }

    @PostMapping("/isInRegion")
    public boolean checkRegion(@RequestBody IsInRegionRequest regionRequest) {
        try {
            LngLat point = regionRequest.getPosition();
            Region region = regionRequest.getRegion();

            if (point == null || region == null) {
                throw new IllegalArgumentException("Position or vertices cannot be null");
            }
            List<LngLat> vertices = region.getVertices();
            if (vertices == null || vertices.size() < 4) {
                throw new IllegalArgumentException("Closed region must contain at least 4 vertices");
            }

            if (point.getLat() == null || point.getLng() == null) {
                throw new IllegalArgumentException("Long or Lat cannot be null");
            }

            if (!isValidCoordinate(point.getLat(), point.getLng())) {
                throw new IllegalArgumentException("Longitude and latitude must not be null and must be valid");
            }
            for (int i = 0; i < vertices.size(); i++) {
                LngLat vertex = vertices.get(i);
                if (vertex == null || !isValidCoordinate(vertex.getLat(), vertex.getLng())) {
                    throw new IllegalArgumentException("All vertices must have valid latitude and longitude values.");
                }
            }

            double[] lats = new double[vertices.size()];
            double[] lngs = new double[vertices.size()];

            for (int i = 0; i < vertices.size(); i++) {
                lats[i] = vertices.get(i).getLat();
                lngs[i] = vertices.get(i).getLng();
            }

            if (!validClosedRegion(lats, lngs)) {
                throw new IllegalArgumentException("Closed region must contain at least 4 non-collinear points to be valid");
            }

            for (LngLat vertex : vertices) {
                if (point.getLat().equals(vertex.getLat()) && point.getLng().equals(vertex.getLng())) {
                    return true; // Point coincides with a vertex, so it's inside the region
                }
            }

            return isPointInPolygon(lats, lngs, point.getLat(), point.getLng());
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    //Actual method to find if it is in the polygon, ray gun method
    private boolean isPointInPolygon(double[] lats, double[] lngs, double lat, double lng) {
        int nvert = lats.length;
        boolean c = false;

        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = -Double.MAX_VALUE;

        for (int i = 0; i < nvert; i++) {
            if (lats[i] < minLat) {
                minLat = lats[i];
            }
            if (lats[i] > maxLat) {
                maxLat = lats[i];
            }
            if (lngs[i] < minLng) {
                minLng = lngs[i];
            }
            if (lngs[i] > maxLng) {
                maxLng = lngs[i];
            }
        }

        if (lat < minLat || lat > maxLat || lng < minLng || lng > maxLng) {
            return false;


        }

        for(int i=0;i<nvert-2;i++){
            double x1 = lngs[i];  // x represents longitude
            double y1 = lats[i];   // y represents latitude
            double x2 = lngs[i + 1];
            double y2 = lats[i + 1];


            double gradient = (y2-y1)/(x2-x1);
            double expectedY = gradient * (lng - x1) + y1;

            if (Math.abs(expectedY - lat) < 1e-9){
                return true;
            }

        }
        //Using the ray gun algorithm
        for (int i = 0, j = nvert - 1; i < nvert; j = i++) {
            if (((lats[i] > lat) != (lats[j] > lat)) &&
                    (lng < (lngs[j] - lngs[i]) * (lat - lats[i]) / (lats[j] - lats[i]) + lngs[i])) {
                c = !c;
            }
        }
        return c;


    }

    //Validation checking if it is a valid closed region
    private boolean validClosedRegion(double[] lats, double[] lngs) {
        int nverts = lats.length;
        if (nverts < 4) {
            return false;
        }

        // Check if the region is closed: first and last vertices must be the same
        if (lats[0] != lats[nverts - 1] || lngs[0] != lngs[nverts - 1]) {
            return false; // Region is not closed, as first and last vertices differ
        }

        // Check for collinearity: all vertices have the same latitude or longitude
        boolean allSameLat = true;
        boolean allSameLng = true;

        for (int i = 1; i < nverts; i++) {
            if (lats[i] != lats[0]) {
                allSameLat = false; // Not all latitudes are the same
            }
            if (lngs[i] != lngs[0]) {
                allSameLng = false; // Not all longitudes are the same
            }
            if (!allSameLat && !allSameLng) {
                break; // No need to check further
            }
        }

        // If all latitudes are the same or all longitudes are the same, they are collinear
        if (allSameLat || allSameLng) {
            return false; // All vertices are collinear
        }

        // Check that there are at least 3 unique vertices
        Set<String> uniqueVertices = new HashSet<>();
        for (int i = 0; i < nverts - 1; i++) { // Exclude the last vertex since it's the same as the first
            uniqueVertices.add(lats[i] + "," + lngs[i]); // Use the lat,lng pair as a unique key
        }

        if (uniqueVertices.size() < 3) {
            return false; // Not enough unique vertices to form a valid polygon
        }

        return true;
    }


    //The code for the second and final coursework begins here
    @PostMapping("/validateOrder")
    public OrderValidationResult orderValidation(@RequestBody Order order) {

        OrderValidator orderValidator = new OrderValidator();


        if(!orderValidator.emptyOrder(order)){
            return new OrderValidationResult(OrderStatus.INVALID,OrderValidationCode.EMPTY_ORDER);
        }
        //Validation for invalid credit card details
        if(order.getCreditCardInformation().getCreditCardNumber().length()!= 16 ) {
            return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.CARD_NUMBER_INVALID);
        }
        if(!orderValidator.isExpiryDateValid(order.getCreditCardInformation().getCreditCardExpiry())){
            return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.EXPIRY_DATE_INVALID);
        }
        if(order.getCreditCardInformation().getCvv().length()!= 3) {
            return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.CVV_INVALID);
        }
        if(!orderValidator.validateTotalPrice(order)){
            return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.TOTAL_INCORRECT);
        }
        if(!orderValidator.menuValidate(order, RestClient.getAllRestaurants())){
            return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.PIZZA_NOT_DEFINED);
        }
        if(!orderValidator.maxPizzas(order)) {
            return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        }
        if(!orderValidator.restaurantStatus(order, RestClient.getAllRestaurants())){
            return new OrderValidationResult(OrderStatus.INVALID,OrderValidationCode.RESTAURANT_CLOSED);
        }
        if(!orderValidator.priceCheck(order, RestClient.getAllRestaurants())){
            return new OrderValidationResult(OrderStatus.INVALID,OrderValidationCode.PRICE_FOR_PIZZA_INVALID);
        }

        if(!orderValidator.diffRestaurants(order,RestClient.getAllRestaurants())){
            return new OrderValidationResult(OrderStatus.INVALID,OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
        }

        return new OrderValidationResult(OrderStatus.VALID, OrderValidationCode.NO_ERROR);
    }


    @PostMapping("/calcDeliveryPath")
    public LngLat [] path (@RequestBody Order order){


        try {

            if (orderValidation(order).getOrderStatus() != OrderStatus.VALID) {
                throw new IllegalArgumentException("Order is not valid");
            }

            OrderValidator orderValidator = new OrderValidator();
            LngLat restaurantPosition = orderValidator
                    .findRestaurant(order.getPizzasInOrder().getFirst().getName(), RestClient.getAllRestaurants())
                    .getLocation();


            LngLat destination = new LngLat(-3.186874, 55.944494); // Destination coordinates
            DronePathFinding dronePathFinding = new DronePathFinding();

            // Delegate the entire pathfinding task to the algorithm
            List<LngLat> path = dronePathFinding.pathFinder(
                    restaurantPosition,
                    RestClient.getAllNoFlyZones(),
                    RestClient.getCentralArea(),
                    order
            );


            return path.toArray(new LngLat[0]);
        } catch (Exception e) {


            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }



    @PostMapping("/calcDeliveryPathAsGeoJson")
    public String flightPath(@RequestBody Order order){
        try{
            if(orderValidation(order).getOrderStatus() != OrderStatus.VALID){
                throw new IllegalArgumentException("Order is not valid");
            }


            OrderValidator orderValidator = new OrderValidator();
            LngLat restaurantPosition = orderValidator.findRestaurant(order.getPizzasInOrder().getFirst().getName(), RestClient.getAllRestaurants()).getLocation();

            DronePathFinding dronePathFinding = new DronePathFinding();
            List<LngLat> path = dronePathFinding.pathFinder(restaurantPosition, RestClient.getAllNoFlyZones(), RestClient.getCentralArea(), order);

            return convertPathToGeoJson(path);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


    private String convertPathToGeoJson(List<LngLat> flightPath) throws JsonProcessingException {
        if (flightPath == null || flightPath.isEmpty()) {
            throw new IllegalArgumentException("Flight path cannot be null or empty.");
        }

        // Omit hovering steps (positions where the drone doesn't move)
        List<LngLat> pathWithoutHovering = new ArrayList<>();
        LngLat previousPosition = null;


        pathWithoutHovering.add(flightPath.getFirst());

        for(LngLat pos: flightPath) {
            if(previousPosition != null) {

                pathWithoutHovering.add(pos);

            }
            previousPosition = pos;


        }

        // Build GeoJSON structure
        Map<String, Object> geoJson = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order
        geoJson.put("type", "FeatureCollection");

        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "LineString");

        // Build coordinates array
        List<List<Double>> coordinates = new ArrayList<>();
        for (LngLat pos : pathWithoutHovering) {
            List<Double> coordinate = Arrays.asList(pos.getLng(), pos.getLat());
            coordinates.add(coordinate);
        }

        geometry.put("coordinates", coordinates);
        feature.put("geometry", geometry);
        feature.put("properties", new HashMap<>());

        geoJson.put("features", Collections.singletonList(feature));

        // Convert to JSON string
        ObjectMapper mapper = new ObjectMapper();
        String geoJsonString = mapper.writeValueAsString(geoJson);

        return geoJsonString;
    }

    private long logTime(String label, long startTime) {
        long now = System.nanoTime();
        System.out.println(label + ": " + (now - startTime) / 1_000_000.0 + " ms");
        return now; // Return the current time to chain with the next checkpoint
    }

}
