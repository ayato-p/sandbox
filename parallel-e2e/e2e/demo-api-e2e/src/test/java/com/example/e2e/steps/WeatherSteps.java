package com.example.e2e.steps;

import com.example.e2e.utils.HttpHelper;
import com.example.e2e.utils.WireMockHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.datastore.ScenarioDataStore;
import com.thoughtworks.gauge.datastore.SpecDataStore;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherSteps {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String DEMO_API_BASE_URL =
            System.getenv("demo_api_base_url") != null
                    ? System.getenv("demo_api_base_url")
                    : "http://localhost:3000";

    private final HttpHelper httpHelper = new HttpHelper(DEMO_API_BASE_URL);

    private WireMockHelper getWireMockHelper() {
        return (WireMockHelper) SpecDataStore.get("wireMockHelper");
    }

    private void storeResponse(HttpResponse<String> response) {
        ScenarioDataStore.put("lastResponse", response);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> getStoredResponse() {
        return (HttpResponse<String>) ScenarioDataStore.get("lastResponse");
    }

    // --- Stub registration steps ---

    @Step("Register stub for <region> on <date> returning <weather> weather with temp <high>/<low>, humidity <humidity>, wind <windSpeed> <windDirection>")
    public void registerWeatherStub(String region, String date, String weather,
                                     int high, int low, int humidity,
                                     int windSpeed, String windDirection) throws Exception {
        String description = weather + " weather in " + region;
        String body = WireMockHelper.buildWeatherResponse(
                region, date, weather, high, low, humidity, windSpeed, windDirection, description);
        getWireMockHelper().registerStub(region, date, body, 200);
    }

    @Step("Register stub for <region> on <date> returning <weather> weather")
    public void registerSimpleWeatherStub(String region, String date, String weather) throws Exception {
        String body = WireMockHelper.buildWeatherResponse(
                region, date, weather, 25, 15, 50, 10, "N", weather + " weather in " + region);
        getWireMockHelper().registerStub(region, date, body, 200);
    }

    @Step("Register stub for <region> on <date> returning status <status> with error <error>")
    public void registerErrorStub(String region, String date, int status, String error) throws Exception {
        String body = WireMockHelper.buildErrorResponse(error);
        getWireMockHelper().registerStub(region, date, body, status);
    }

    @Step("Register stub for <region> on <date> returning status <status> with body <body>")
    public void registerStubWithBody(String region, String date, int status, String body) throws Exception {
        getWireMockHelper().registerStub(region, date, body, status);
    }

    @Step("Register stub for <region> on <date> with delay <delayMs> ms returning <weather> weather")
    public void registerDelayedStub(String region, String date, int delayMs, String weather) throws Exception {
        String body = WireMockHelper.buildWeatherResponse(
                region, date, weather, 25, 15, 50, 10, "N", weather + " weather in " + region);
        getWireMockHelper().registerStubWithDelay(region, date, body, 200, delayMs);
    }

    @Step("Register stub for <region> on <date> with delay <delayMs> ms returning status <status>")
    public void registerDelayedErrorStub(String region, String date, int delayMs, int status) throws Exception {
        String body = WireMockHelper.buildErrorResponse("Delayed error");
        getWireMockHelper().registerStubWithDelay(region, date, body, status, delayMs);
    }

    // --- Request steps ---

    @Step("Request weather for <region> on <date>")
    public void requestWeather(String region, String date) throws Exception {
        HttpResponse<String> response = httpHelper.getWeather(region, date);
        storeResponse(response);
    }

    @Step("Request weather without region parameter for date <date>")
    public void requestWeatherWithoutRegion(String date) throws Exception {
        HttpResponse<String> response = httpHelper.get("/weather?date=" + date);
        storeResponse(response);
    }

    @Step("Request weather without date parameter for region <region>")
    public void requestWeatherWithoutDate(String region) throws Exception {
        HttpResponse<String> response = httpHelper.get("/weather?region=" + region);
        storeResponse(response);
    }

    @Step("Request weather without any parameters")
    public void requestWeatherWithoutParams() throws Exception {
        HttpResponse<String> response = httpHelper.get("/weather");
        storeResponse(response);
    }

    @Step("Request health check")
    public void requestHealthCheck() throws Exception {
        HttpResponse<String> response = httpHelper.getHealth();
        storeResponse(response);
    }

    // --- Verification steps ---

    @Step("The response status code should be <statusCode>")
    public void verifyStatusCode(int statusCode) {
        HttpResponse<String> response = getStoredResponse();
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    @Step("The response body should contain <key> with value <value>")
    public void verifyResponseField(String key, String value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.has(key)).as("Response should have field: " + key).isTrue();
        assertThat(json.get(key).asText()).isEqualTo(value);
    }

    @Step("The response body should contain <key>")
    public void verifyResponseHasField(String key) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.has(key)).as("Response should have field: " + key).isTrue();
    }

    @Step("The response weather should be <weather>")
    public void verifyWeather(String weather) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("weather").asText()).isEqualTo(weather);
    }

    @Step("The response region should be <region>")
    public void verifyRegion(String region) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("region").asText()).isEqualTo(region);
    }

    @Step("The response date should be <date>")
    public void verifyDate(String date) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("date").asText()).isEqualTo(date);
    }

    @Step("The response should have a requestTimestamp")
    public void verifyHasRequestTimestamp() throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.has("requestTimestamp")).as("Response should have requestTimestamp").isTrue();
        assertThat(json.get("requestTimestamp").asText()).isNotEmpty();
    }

    @Step("The response error should contain <message>")
    public void verifyErrorMessage(String message) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.has("error")).as("Response should have error field").isTrue();
        assertThat(json.get("error").asText()).contains(message);
    }

    @Step("The response high temperature should be <value>")
    public void verifyHighTemp(int value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("temperature").get("high").asInt()).isEqualTo(value);
    }

    @Step("The response low temperature should be <value>")
    public void verifyLowTemp(int value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("temperature").get("low").asInt()).isEqualTo(value);
    }

    @Step("The response humidity should be <value>")
    public void verifyHumidity(int value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("humidity").asInt()).isEqualTo(value);
    }

    @Step("The response wind speed should be <value>")
    public void verifyWindSpeed(int value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("wind").get("speed").asInt()).isEqualTo(value);
    }

    @Step("The response wind direction should be <value>")
    public void verifyWindDirection(String value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("wind").get("direction").asText()).isEqualTo(value);
    }

    @Step("The response description should be <description>")
    public void verifyDescription(String description) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("description").asText()).isEqualTo(description);
    }

    @Step("The response humidity should be between <min> and <max>")
    public void verifyHumidityRange(int min, int max) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        int humidity = json.get("humidity").asInt();
        assertThat(humidity).isBetween(min, max);
    }

    @Step("The response high temperature should be greater than <value>")
    public void verifyHighTempGreaterThan(int value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("temperature").get("high").asInt()).isGreaterThan(value);
    }

    @Step("The response low temperature should be less than high temperature")
    public void verifyLowLessThanHigh() throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        int high = json.get("temperature").get("high").asInt();
        int low = json.get("temperature").get("low").asInt();
        assertThat(low).isLessThan(high);
    }

    @Step("The response wind speed should be greater than <value>")
    public void verifyWindSpeedGreaterThan(int value) throws Exception {
        HttpResponse<String> response = getStoredResponse();
        JsonNode json = mapper.readTree(response.body());
        assertThat(json.get("wind").get("speed").asInt()).isGreaterThan(value);
    }
}
