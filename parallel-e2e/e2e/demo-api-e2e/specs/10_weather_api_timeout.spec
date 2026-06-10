# Weather API Timeout

* Register stub for "okinawa" on "2026-06-10" with delay "10000" ms returning "sunny" weather

## Weather API times out
* Request weather for "okinawa" on "2026-06-10"
* The response status code should be "504"
* The response error should contain "timeout"
