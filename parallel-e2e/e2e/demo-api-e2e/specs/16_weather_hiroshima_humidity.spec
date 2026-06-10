# Weather Tokyo Humidity

* Register stub for "tokyo" on "2026-06-01" returning "rainy" weather with temp "26"/"21", humidity "92", wind "5" "W"

## Verify high humidity for Tokyo
* Request weather for "tokyo" on "2026-06-01"
* The response status code should be "200"
* The response region should be "tokyo"
* The response weather should be "rainy"
* The response humidity should be "92"
* The response humidity should be between "80" and "100"
* The response should have a requestTimestamp
