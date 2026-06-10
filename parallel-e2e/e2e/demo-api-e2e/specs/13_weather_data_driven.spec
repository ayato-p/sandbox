# Weather Data Driven Test

   |region  |date       |weather |
   |--------|-----------|--------|
   |tokyo   |2026-06-01 |sunny   |
   |osaka   |2026-06-02 |rainy   |
   |nagoya  |2026-06-03 |cloudy  |
   |tokyo   |2026-06-01 |cloudy  |

## Verify weather for multiple regions

* Register stub for <region> on <date> returning <weather> weather
* Request weather for <region> on <date>
* The response status code should be "200"
* The response region should be <region>
* The response date should be <date>
* The response weather should be <weather>
