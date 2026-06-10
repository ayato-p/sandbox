# Weather Osaka Temperature

* Register stub for "osaka" on "2026-06-02" returning "sunny" weather with temp "38"/"26", humidity "40", wind "3" "S"

## Verify high temperature for Osaka
* Request weather for "osaka" on "2026-06-02"
* The response status code should be "200"
* The response region should be "osaka"
* The response high temperature should be "38"
* The response low temperature should be "26"
* The response high temperature should be greater than "35"
* The response low temperature should be less than high temperature
* The response should have a requestTimestamp
