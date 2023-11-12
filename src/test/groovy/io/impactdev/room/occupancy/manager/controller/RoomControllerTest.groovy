package io.impactdev.room.occupancy.manager.controller

import io.impactdev.room.occupancy.manager.BaseIntTest
import io.impactdev.room.occupancy.manager.ControllerAdvisor.ErrorResponse
import io.impactdev.room.occupancy.manager.controller.model.CalculateOptimalRoomOccupancyRequest
import io.impactdev.room.occupancy.manager.controller.model.CalculateOptimalRoomOccupancyResponse
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers

class RoomControllerTest extends BaseIntTest {

    def "should return 400 when calculate optimal room occupancy request is invalid"() {
        given:
        def request = new CalculateOptimalRoomOccupancyRequest(premiumRoomsCount, economyRoomsCount, guestPrices)

        when:
        ValidatableResponse response = performCalculateOptimalRoomOccupancyCall(request)

        then:
        response.statusCode(HttpStatus.SC_BAD_REQUEST)

        and:
        //rest assured json body matcher doesn't support paths like 'guest_prices[0]' is it's not a collection
        def errorResponse = objectMapper.readValue(response.extract().body().asString(), ErrorResponse)
        errorResponse.errors().isEmpty()
        errorResponse.fieldErrors().size() == 1
        errorResponse.fieldErrors()[expectedFieldPath] == expectedFieldError

        where:
        premiumRoomsCount | economyRoomsCount | guestPrices                     || expectedFieldPath     || expectedFieldError
        null              | 0                 | [1.00]                          || "premium_rooms_count" || "Premium rooms count cannot be null"
        -1                | 0                 | [1.00]                          || "premium_rooms_count" || "Premium rooms count cannot be lower than 0"
        1001              | 0                 | [1.00]                          || "premium_rooms_count" || "Premium rooms count cannot be greater than 1000"
        0                 | null              | [1.00]                          || "economy_rooms_count" || "Economy rooms count cannot be null"
        0                 | -1                | [1.00]                          || "economy_rooms_count" || "Economy rooms count cannot be lower than 0"
        0                 | 1001              | [1.00]                          || "economy_rooms_count" || "Economy rooms count cannot be greater than 1000"
        0                 | 0                 | null                            || "guest_prices"        || "Guests prices list cannot be null or empty"
        0                 | 0                 | []                              || "guest_prices"        || "Guests prices list cannot be null or empty"
        0                 | 0                 | Collections.nCopies(1001, 1.00) || "guest_prices"        || "Guests prices list cannot have more than 1000 elements"
        0                 | 0                 | [-0.01]                         || "guest_prices[0]"     || "Guest price must be positive number"
        0                 | 0                 | [1000000.00]                    || "guest_prices[0]"     || "Guest price cannot be greater than 999999.99 or have more than 2 fractions"
        0                 | 0                 | [0.001]                         || "guest_prices[0]"     || "Guest price cannot be greater than 999999.99 or have more than 2 fractions"
    }

    def "should calculate most profitable room occupancy"() {
        given:
        def request = new CalculateOptimalRoomOccupancyRequest(
                premiumRoomsCount,
                economyRoomsCount,
                [23.00, 45.00, 155.00, 374.00, 22.00, 99.99, 100.00, 101.00, 115.00, 209.00])

        and:
        def expectedResponse = new CalculateOptimalRoomOccupancyResponse(
                expectedUsedPremiumRoomsCount,
                expectedUsedEconomyRoomsCount,
                expectedPremiumRoomsProfits,
                expectedEconomyRoomsProfits)

        when:
        def response = performCalculateOptimalRoomOccupancyCall(request)

        then:
        response.statusCode(HttpStatus.SC_OK)
        verifyResponse(response, expectedResponse)

        where:
        premiumRoomsCount | economyRoomsCount || expectedUsedPremiumRoomsCount || expectedUsedEconomyRoomsCount || expectedPremiumRoomsProfits || expectedEconomyRoomsProfits
        //challenge provided tests cases
        3                 | 3                 || 3                             || 3                             || 738.00                      || 167.99
        7                 | 5                 || 6                             || 4                             || 1054.00                     || 189.99
        2                 | 7                 || 2                             || 4                             || 583.00                      || 189.99
        7                 | 1                 || 7                             || 1                             || 1153.99                     || 45.00
        //additional tests cases
        0                 | 0                 || 0                             || 0                             || 0.00                        || 0.00
        1                 | 0                 || 1                             || 0                             || 374.00                      || 0.00
        0                 | 1                 || 0                             || 1                             || 0.00                        || 99.99
        0                 | 5                 || 0                             || 4                             || 0.00                        || 189.99
        6                 | 0                 || 6                             || 0                             || 1054.00                     || 0.00
        10                | 0                 || 10                            || 0                             || 1243.99                     || 0.00
    }

    private ValidatableResponse performCalculateOptimalRoomOccupancyCall(CalculateOptimalRoomOccupancyRequest request) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/rooms/calculate-optimal-occupancy")
                .then()
    }

    private void verifyResponse(ValidatableResponse response, CalculateOptimalRoomOccupancyResponse expectedResponse) {
        response.body("used_premium_rooms_count", Matchers.equalTo(expectedResponse.usedPremiumRoomsCount()))
        response.body("used_economy_rooms_count", Matchers.equalTo(expectedResponse.usedEconomyRoomsCount()))
        response.body("premium_rooms_profits", Matchers.equalTo(expectedResponse.premiumRoomsProfits().floatValue()))
        response.body("economy_rooms_profits", Matchers.equalTo(expectedResponse.economyRoomsProfits().floatValue()))
    }
}
