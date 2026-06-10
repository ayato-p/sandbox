# Weather Weekly Forecast

   |region  |date       |weather |
   |--------|-----------|--------|
   |tokyo   |2026-06-01 |sunny   |
   |osaka   |2026-06-02 |cloudy  |
   |nagoya  |2026-06-03 |rainy   |
   |tokyo   |2026-06-01 |rainy   |

## Verify weather for multiple dates

* Register stub for <region> on <date> returning <weather> weather
* Request weather for <region> on <date>
* The response status code should be "200"
* The response region should be <region>
* The response date should be <date>
* The response weather should be <weather>
