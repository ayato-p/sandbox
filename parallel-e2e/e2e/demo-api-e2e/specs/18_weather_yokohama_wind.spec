# Weather Nagoya Wind

* Register stub for "nagoya" on "2026-06-03" returning "windy" weather with temp "22"/"16", humidity "50", wind "35" "NE"

## Verify strong wind for Nagoya
* Request weather for "nagoya" on "2026-06-03"
* The response status code should be "200"
* The response region should be "nagoya"
* The response weather should be "windy"
* The response wind speed should be "35"
* The response wind direction should be "NE"
* The response wind speed should be greater than "30"
* The response should have a requestTimestamp
