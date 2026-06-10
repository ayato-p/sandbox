# Weather Invalid Date Format

* Register stub for "tokyo" on "2026/06/01" returning status "400" with error "Invalid date format"
* Register stub for "tokyo" on "abc" returning status "400" with error "Invalid date format"

## Invalid date format with slashes
* Request weather for "tokyo" on "2026/06/01"
* The response status code should be "400"

## Invalid date format with text
* Request weather for "tokyo" on "abc"
* The response status code should be "400"
