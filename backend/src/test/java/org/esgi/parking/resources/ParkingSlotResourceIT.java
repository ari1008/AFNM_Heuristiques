package org.esgi.parking.resources;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestHTTPEndpoint(ParkingSlotResource.class)
class ParkingSlotResourceIT {


    @Test
    void allSlots_returns_list() {
        when()
                .get()
                .then()
                .statusCode(200)
                .body("$.size()", greaterThan(0));
    }


    @Test
    void available_missing_params_returns_400() {
        when()
                .get("/available")
                .then()
                .statusCode(400);
    }


    @Test
    void available_bad_range_returns_400() {
        given()
                .queryParam("from", "2030-01-02")
                .queryParam("to",   "2030-01-01")
                .when()
                .get("/available")
                .then()
                .statusCode(400);
    }


    @Test
    void available_ok_returns_200_and_array() {
        given()
                .queryParam("from", "2030-01-01")
                .queryParam("to",   "2030-01-02")
                .when()
                .get("/available")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(0));
    }
}