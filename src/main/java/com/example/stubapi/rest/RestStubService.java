package com.example.stubapi.rest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RestStubService {

    private final Map<LookupKey, RestStubProperties.Response> responseRegistry = new LinkedHashMap<>();

    public RestStubService(RestStubProperties properties) {
        for (RestStubProperties.Response response : properties.getResponses()) {
            if (response.getPath() == null) {
                continue;
            }
            HttpMethod method = Optional.ofNullable(response.getMethod()).orElse(HttpMethod.GET);
            LookupKey key = new LookupKey(method, normalizePath(response.getPath()));
            responseRegistry.put(key, response);
        }
    }

    public Optional<RestStubProperties.Response> find(HttpMethod method, String path) {
        if (method == null || path == null) {
            return Optional.empty();
        }
        LookupKey key = new LookupKey(method, normalizePath(path));
        RestStubProperties.Response response = responseRegistry.get(key);
        return Optional.ofNullable(response);
    }

    private static String normalizePath(String rawPath) {
        if (!StringUtils.hasText(rawPath)) {
            return "/";
        }
        String trimmed = rawPath.trim();
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private record LookupKey(HttpMethod method, String path) {
        private LookupKey {
            if (method == null) {
                method = HttpMethod.GET;
            }
            if (path == null) {
                path = "/";
            }
        }
    }
}
