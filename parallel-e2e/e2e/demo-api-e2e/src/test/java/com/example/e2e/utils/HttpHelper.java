package com.example.e2e.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpHelper {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl;

    public HttpHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getWeather(String region, String date) throws Exception {
        String path = "/weather?region=" + region + "&date=" + date;
        return get(path);
    }

    public HttpResponse<String> getHealth() throws Exception {
        return get("/health");
    }
}
