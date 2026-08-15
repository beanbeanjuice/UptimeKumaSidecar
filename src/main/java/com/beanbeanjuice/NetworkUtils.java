package com.beanbeanjuice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class NetworkUtils {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    public static CompletableFuture<Response> pull(String url) {
        URI uri = URI.create(url);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .timeout(REQUEST_TIMEOUT)
                .GET();

        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isEmpty()) {
            String encoded = Base64.getEncoder().encodeToString(userInfo.getBytes(StandardCharsets.UTF_8));
            builder.header("Authorization", "Basic " + encoded);
        }

        HttpRequest request = builder.build();

        long now = System.currentTimeMillis();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply((response) -> {
            long elapsed = System.currentTimeMillis() - now;
            return new Response((int) elapsed, response.statusCode());
        }).exceptionally((e) -> {
            System.out.println("Error contacting service.");
            throw new CompletionException(e.getCause());
        });
    }

    public static void push(String url, Response data, boolean usePing) {
        String status = data.statusCode() == 200 ? "up" : "down";
        String msg = String.format("%s", data.statusCode());
        String ping = String.format("%s", data.ping());

        url += String.format("?status=%s", status);
        url += String.format("&msg=%s", msg);

        if (usePing) {
            url += String.format("&ping=%s", ping);
        }

        System.out.printf("Using URL: %s\n", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();

        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).exceptionally((e) -> {
            System.out.println("Error contacting Uptime Kuma.");
            throw new CompletionException(e.getCause());
        });
    }

}
