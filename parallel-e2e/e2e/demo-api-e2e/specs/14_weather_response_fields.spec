# Weather Response Fields Validation

* Register stub for "osaka" on "2026-06-02" returning "sunny" weather with temp "30"/"20", humidity "45", wind "8" "S"

## Verify all response fields for Osaka
* Request weather for "osaka" on "2026-06-02"
* The response status code should be "200"
* The response region should be "osaka"
* The response date should be "2026-06-02"
* The response weather should be "sunny"
* The response high temperature should be "30"
* The response low temperature should be "20"
* The response humidity should be "45"
* The response wind speed should be "8"
* The response wind direction should be "S"
* The response description should be "sunny weather in osaka"
* The response should have a requestTimestamp
* The response low temperature should be less than high temperature
