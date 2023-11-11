package io.impactdev.room.occupancy.manager

import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.hamcrest.Matchers

class RoomOccupancyManagerApplicationTest extends BaseIntTest {

    def "should initialize context and expose health endpoint"() {
        expect:
        RestAssured.when()
                .get("/actuator/health")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("status", Matchers.equalTo("UP"))
    }
}
