# room-occupancy-manager

**Requirements:**

java 17, groovy 4, gradle

**How to run checkstyle**

gradle clean checkstyleMain

**How to run test**

gradle clean test

**How to run application**

gradle clean bootRun

Swagger url: http://localhost:8080/room-occupancy-manager/swagger-ui/index.html


**Notes**

Solving Room Occupancy Manager code challenge.

I've used a fairly simple solution using sorting before calculating 
optimal rooms occupancy. It should be suitable for most cases, 
yet strategy different algorithms for case specific usages could be added, 
especially if numer of guest would be a lot bigger than the number of rooms.

Since the code is quite simple, I've only added integration tests 
to verify controller requests validation and complete output.

Added swagger for easier api checking and 
a bit modified Google based checkstyle just to keep the project code clean.

**P.S.:**
There is a error in the last test case data of the provided challenge description.

The results should be:

**Test 4**

● (input) Free Premium rooms: 7

● (input) Free Economy rooms: 1

● (output) Usage Premium: 7 (EUR 1153.99) instead of (EUR 1153)

● (output) Usage Economy: 1 (EUR 45) instead of (EUR 45.99)

Since none of the input data: [23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209] is 45.99.

