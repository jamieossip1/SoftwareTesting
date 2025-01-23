package com.example.ilpcoursework;

import com.example.ilpcoursework.Controller.BasicController;
import com.example.ilpcoursework.Data.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//Test
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CW1Tests {

    @Autowired
    private BasicController basicController;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() throws Exception {
        assertThat(basicController).isNotNull();
    }

    @Test
    void isAliveShouldReturnTrue() {
        String response = this.restTemplate.getForObject("http://localhost:" + port + "/isAlive", String.class);
        assertThat(response).isEqualTo("true");
    }

    @Test
    void studentIDs2415872() {
        String response = this.restTemplate.getForObject("http://localhost:" + port + "/uuid", String.class);
        assertThat(response).contains("s2415872");
    }


    /*
    @Test
    void studentIDWrong() {
        String response = this.restTemplate.getForObject("http://localhost:" + port + "/uuid", String.class);
        assertThat(response).isNotEqualTo("s2415872");
    }
    */

//Tests for the distanceTo endpoint:
@Test
void distanceToSameLocation() {
    LngLatPairRequest request = new LngLatPairRequest(
            new LngLat(0.0, 0.0),
            new LngLat(0.0, 0.0)
    );
    double response = this.restTemplate.postForObject("http://localhost:" + port + "/distanceTo", request, Double.class);
    assertThat(response).isEqualTo(0.0); // Assert exact value
}

    @Test
    void distanceToHorizontalDistance() {
        LngLatPairRequest request = new LngLatPairRequest(
                new LngLat(0.0, 0.0),
                new LngLat(0.0, 1.0)
        );
        double response = this.restTemplate.postForObject("http://localhost:" + port + "/distanceTo", request, Double.class);

        double expected = 1.0; // The horizontal difference in degrees is exactly 1
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void distanceToVerticalDistance() {
        LngLatPairRequest request = new LngLatPairRequest(
                new LngLat(0.0, 0.0),
                new LngLat(1.0, 0.0)
        );
        double response = this.restTemplate.postForObject("http://localhost:" + port + "/distanceTo", request, Double.class);

        double expected = 1.0; // The vertical difference in degrees is exactly 1
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void distanceToDiagonalDistance() {
        LngLatPairRequest request = new LngLatPairRequest(
                new LngLat(0.0, 0.0),
                new LngLat(3.0, 4.0)
        );
        double response = this.restTemplate.postForObject("http://localhost:" + port + "/distanceTo", request, Double.class);

        // Using the Pythagorean theorem to calculate the diagonal distance
        double expected = Math.sqrt(Math.pow(3.0, 2) + Math.pow(4.0, 2)); // 5.0
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void distanceToEdgeCaseExactCalculation() {
        LngLatPairRequest request = new LngLatPairRequest(
                new LngLat(-180.0, -90.0),
                new LngLat(180.0, 90.0)
        );
        double response = this.restTemplate.postForObject("http://localhost:" + port + "/distanceTo", request, Double.class);

        // Calculate the expected value for this specific case
        double expected = Math.sqrt(Math.pow(180.0 - (-180.0), 2) + Math.pow(90.0 - (-90.0), 2)); // sqrt(360^2 + 180^2)
        assertThat(response).isEqualTo(expected);
    }

    //Tests for the isCloseTo endpoint

    @Test
    void isCloseToValidInputsWithinThreshold() {
        LngLatPairRequest request = new LngLatPairRequest(new LngLat(0.0, 0.0), new LngLat(0.0001, 0.0001));
        boolean response = this.restTemplate.postForObject("http://localhost:" + port + "/isCloseTo", request, Boolean.class);
        assertThat(response).isTrue();
    }

    @Test
    void isCloseToValidInputsOutsideThreshold() {
        LngLatPairRequest request = new LngLatPairRequest(new LngLat(0.0, 0.0), new LngLat(1.0, 1.0));
        boolean response = this.restTemplate.postForObject("http://localhost:" + port + "/isCloseTo", request, Boolean.class);
        assertThat(response).isFalse();
    }

    @Test
    void isCloseToInvalidInputs() {
        LngLatPairRequest request = new LngLatPairRequest(null, new LngLat(1.0, 1.0));
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/isCloseTo", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }


//Test cases for nextPosition endpoint

    @Test
    void nextPositionExactAnswer() {
        // Create a valid request with known input values
        NextPositionRequest request = new NextPositionRequest();
        request.setAngle(45.0);
        request.setStart(new LngLat(-3.192473, 55.946233));

        // Send the POST request to the /nextPosition endpoint
        ResponseEntity<LngLat> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/nextPosition",
                request,
                LngLat.class
        );

        // Assert the response status code is 200 OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert the calculated next position matches the expected exact values
        LngLat expectedPosition = new LngLat(-3.192366933982822, 55.946339066017174); // Expected new position
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getLng()).isEqualTo(expectedPosition.getLng());
        assertThat(response.getBody().getLat()).isEqualTo(expectedPosition.getLat());
    }
    @Test
    void nextPositionExactSouthMovement() {
        // Create a request to move directly south
        NextPositionRequest request = new NextPositionRequest();
        request.setAngle(180.0);
        request.setStart(new LngLat(1.0, 1.0));

        // Send the POST request to the /nextPosition endpoint
        ResponseEntity<LngLat> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/nextPosition",
                request,
                LngLat.class
        );

        // Assert the response status code is 200 OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert the calculated position matches the expected exact values
        LngLat expectedPosition = new LngLat(1.0-0.00015, 1.0); // Expected position after moving south
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getLng()).isEqualTo(expectedPosition.getLng());
        assertThat(response.getBody().getLat()).isEqualTo(expectedPosition.getLat());
    }

    @Test
    void nextPositionInvalidAngle() {
        // Create a request with an invalid angle (greater than 360)
        NextPositionRequest request = new NextPositionRequest();
        request.setAngle(-390.0);
        request.setStart(new LngLat(1.0, 1.0));

        // Send the POST request to the /nextPosition endpoint
        ResponseEntity<String> response = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/nextPosition",
                request,
                String.class
        );

        // Assert the response status code is 400 Bad Request
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    }

    //Tests for isInRegion endpoint

    @Test
    void isInRegionPointInside() {
        List<LngLat> vertices = List.of(
                new LngLat(0.0, 0.0),
                new LngLat(0.0, 1.0),
                new LngLat(1.0, 1.0),
                new LngLat(1.0, 0.0),
                new LngLat(0.0, 0.0) // Closing the polygon
        );
        IsInRegionRequest request = new IsInRegionRequest(new LngLat(0.5, 0.5), new Region("Test",vertices));
        boolean response = this.restTemplate.postForObject("http://localhost:" + port + "/isInRegion", request, Boolean.class);
        assertThat(response).isTrue();
    }

    @Test
    void isInRegionPointOnVertex() {
        List<LngLat> vertices = List.of(
                new LngLat(0.0, 0.0),
                new LngLat(0.0, 1.0),
                new LngLat(1.0, 1.0),
                new LngLat(1.0, 0.0),
                new LngLat(0.0, 0.0) // Closing the polygon
        );
        IsInRegionRequest request = new IsInRegionRequest(new LngLat(0.0, 0.0), new Region("Test",vertices));
        boolean response = this.restTemplate.postForObject("http://localhost:" + port + "/isInRegion", request, Boolean.class);
        assertThat(response).isTrue();
    }

    @Test
    void isInRegionPointOutside() {
        List<LngLat> vertices = List.of(
                new LngLat(0.0, 0.0),
                new LngLat(0.0, 1.0),
                new LngLat(1.0, 1.0),
                new LngLat(1.0, 0.0),
                new LngLat(0.0, 0.0) // Closing the polygon
        );
        IsInRegionRequest request = new IsInRegionRequest(new LngLat(2.0, 2.0), new Region("Test",vertices));
        boolean response = this.restTemplate.postForObject("http://localhost:" + port + "/isInRegion", request, Boolean.class);
        assertThat(response).isFalse();
    }

    @Test
    void isInRegionInvalidRegion() {
        List<LngLat> vertices = List.of(
                new LngLat(0.0, 0.0),
                new LngLat(0.0, 1.0) // Not enough vertices to form a polygon
        );
        IsInRegionRequest request = new IsInRegionRequest(new LngLat(0.5, 0.5), new Region("Test",vertices));
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/isInRegion", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }




}
