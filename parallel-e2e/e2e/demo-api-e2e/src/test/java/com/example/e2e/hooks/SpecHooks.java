package com.example.e2e.hooks;

import com.example.e2e.utils.WireMockHelper;
import com.thoughtworks.gauge.BeforeSpec;
import com.thoughtworks.gauge.AfterSpec;
import com.thoughtworks.gauge.ExecutionContext;
import com.thoughtworks.gauge.datastore.SpecDataStore;

public class SpecHooks {

    private static final String WIREMOCK_BASE_URL =
            System.getenv("wiremock_base_url") != null
                    ? System.getenv("wiremock_base_url")
                    : "http://localhost:8080";

    @BeforeSpec
    public void beforeSpec(ExecutionContext context) {
        WireMockHelper helper = new WireMockHelper(WIREMOCK_BASE_URL);
        SpecDataStore.put("wireMockHelper", helper);
    }

    @AfterSpec
    public void afterSpec(ExecutionContext context) {
        WireMockHelper helper = (WireMockHelper) SpecDataStore.get("wireMockHelper");
        if (helper != null) {
            try {
                helper.deleteAllRegisteredStubs();
            } catch (Exception e) {
                System.err.println("Failed to cleanup WireMock stubs: " + e.getMessage());
            }
        }
    }
}
