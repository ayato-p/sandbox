# Weather Missing Parameters

## Missing region parameter
* Request weather without region parameter for date "2026-06-07"
* The response status code should be "400"
* The response error should contain "region"

## Missing date parameter
* Request weather without date parameter for region "tokyo"
* The response status code should be "400"
* The response error should contain "date"

## Missing both parameters
* Request weather without any parameters
* The response status code should be "400"
