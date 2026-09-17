package com.financeapp.finance_app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FinanceAppApplicationTests {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void healthIsPublicAndIncludesDatabaseReadiness() throws Exception {
        var response = get("/actuator/health");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
        assertFalse(response.body().contains("components"));
    }

    @Test
    void browserRoutesServeTheFrontendWithoutAuthentication() throws Exception {
        for (String path : new String[]{"/", "/login", "/register", "/dashboard", "/goals", "/index.html"}) {
            var response = get(path);
            assertEquals(200, response.statusCode(), path);
            assertTrue(response.body().contains("id=\"root\""), path);
        }
    }

    @Test
    void apiAndOtherActuatorEndpointsRemainProtected() throws Exception {
        for (String path : new String[]{"/api/transaction/all", "/api/chat", "/api/does-not-exist", "/actuator/env"}) {
            var response = get(path);
            assertEquals(401, response.statusCode(), path);
            assertFalse(response.body().contains("id=\"root\""), path);
        }
    }

    @Test
    void missingStaticAssetsReturn404InsteadOfTheAppShell() throws Exception {
        var response = get("/assets/missing.js");
        assertEquals(404, response.statusCode());
        assertFalse(response.body().contains("id=\"root\""));
    }

    @Test
    void corsUsesConfiguredOriginAndRejectsOtherOrigins() throws Exception {
        var allowed = preflight("https://frontend.example.test");
        assertEquals(200, allowed.statusCode());
        assertEquals("https://frontend.example.test", allowed.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
        assertEquals(403, preflight("https://untrusted.example.test").statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> preflight(String origin) throws Exception {
        var request = HttpRequest.newBuilder(uri("/api/auth/login"))
                .header("Origin", origin)
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

}
