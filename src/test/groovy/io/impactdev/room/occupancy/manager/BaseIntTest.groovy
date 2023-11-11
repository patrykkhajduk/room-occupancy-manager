package io.impactdev.room.occupancy.manager

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest(classes = RoomOccupancyManagerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class BaseIntTest extends Specification {

    @LocalServerPort
    private Integer port

    @Autowired
    protected ObjectMapper objectMapper

    def setup() {
        RestAssured.baseURI = "http://localhost:$port/room-occupancy-manager"
    }
}
