package com.example.e2e.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WireMockHelper {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String baseUrl;
    private final List<String> registeredStubIds = Collections.synchronizedList(new ArrayList<>());

    public WireMockHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String registerStub(String region, String date, String responseBody, int status) throws Exception {
        return registerStubWithDelay(region, date, responseBody, status, 1000);
    }

    public String registerStubWithDelay(String region, String date, String responseBody, int status, int delayMs)
            throws Exception {
        ObjectNode mapping = mapper.createObjectNode();

        ObjectNode request = mapper.createObjectNode();
        request.put("method", "GET");
        request.put("urlPath", "/weather");

        ObjectNode queryParameters = mapper.createObjectNode();

        ObjectNode regionParam = mapper.createObjectNode();
        regionParam.put("equalTo", region);
        queryParameters.set("region", regionParam);

        ObjectNode dateParam = mapper.createObjectNode();
        dateParam.put("equalTo", date);
        queryParameters.set("date", dateParam);

        request.set("queryParameters", queryParameters);
        mapping.set("request", request);

        ObjectNode response = mapper.createObjectNode();
        response.put("status", status);
        response.put("body", responseBody);

        ObjectNode headers = mapper.createObjectNode();
        headers.put("Content-Type", "application/json");
        response.set("headers", headers);

        if (delayMs > 0) {
            response.put("fixedDelayMilliseconds", delayMs);
        }

        mapping.set("response", response);

        String body = mapper.writeValueAsString(mapping);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/__admin/mappings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JsonNode result = mapper.readTree(httpResponse.body());
        String stubId = result.get("id").asText();
        registeredStubIds.add(stubId);
        return stubId;
    }

    public void deleteStub(String stubId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/__admin/mappings/" + stubId))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void deleteAllRegisteredStubs() throws Exception {
        for (String stubId : registeredStubIds) {
            deleteStub(stubId);
        }
        registeredStubIds.clear();
    }

    public List<String> getRegisteredStubIds() {
        return Collections.unmodifiableList(registeredStubIds);
    }

    public static String buildWeatherResponse(String region, String date, String weather,
                                               int tempHigh, int tempLow, int humidity,
                                               int windSpeed, String windDirection,
                                               String description) throws Exception {
        ObjectMapper m = new ObjectMapper();
        ObjectNode root = m.createObjectNode();
        root.put("region", region);
        root.put("date", date);
        root.put("weather", weather);

        ObjectNode temperature = m.createObjectNode();
        temperature.put("high", tempHigh);
        temperature.put("low", tempLow);
        root.set("temperature", temperature);

        root.put("humidity", humidity);

        ObjectNode wind = m.createObjectNode();
        wind.put("speed", windSpeed);
        wind.put("direction", windDirection);
        root.set("wind", wind);

        root.put("description", description);

        return m.writeValueAsString(root);
    }

    public static String buildErrorResponse(String error) throws Exception {
        ObjectMapper m = new ObjectMapper();
        ObjectNode root = m.createObjectNode();
        root.put("error", error);
        return m.writeValueAsString(root);
    }
}
