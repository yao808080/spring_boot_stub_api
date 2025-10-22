package com.example.stubapi.rest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
@RequestMapping("/stub/rest")
public class RestStubController {

    private final RestStubService restStubService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RestStubController(RestStubService restStubService) {
        this.restStubService = restStubService;
    }

    @RequestMapping("/**")
    public ResponseEntity<?> handle(HttpServletRequest request) {
        HttpMethod method = resolveMethod(request.getMethod());
        String resolvedPath = resolvePath(request);

        Optional<RestStubProperties.Response> stubResponse = restStubService.find(method, resolvedPath);
        if (stubResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "message", "No stub mapping found",
                    "method", method != null ? method.name() : "UNKNOWN",
                    "path", resolvedPath
                ));
        }

        RestStubProperties.Response response = stubResponse.get();
        HttpHeaders headers = new HttpHeaders();
        response.getHeaders().forEach(headers::add);
        HttpStatusCode status = HttpStatusCode.valueOf(response.getStatus());
        return new ResponseEntity<>(response.getBody(), headers, status);
    }

    private HttpMethod resolveMethod(String methodName) {
        if (methodName == null) {
            return null;
        }
        try {
            return HttpMethod.valueOf(methodName);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String resolvePath(HttpServletRequest request) {
        Object pathAttribute = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        Object patternAttribute = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pathAttribute instanceof String pathWithinMapping && patternAttribute instanceof String matchingPattern) {
            String remaining = pathMatcher.extractPathWithinPattern(matchingPattern, pathWithinMapping);
            if (remaining == null || remaining.isEmpty()) {
                return "/";
            }
            if (!remaining.startsWith("/")) {
                remaining = "/" + remaining;
            }
            return remaining;
        }

        String requestUri = request.getRequestURI();
        if (requestUri == null || requestUri.isEmpty()) {
            return "/";
        }
        String remaining = requestUri.replaceFirst("^/stub/rest", "");
        if (remaining == null || remaining.isEmpty()) {
            return "/";
        }
        if (!remaining.startsWith("/")) {
            remaining = "/" + remaining;
        }
        return remaining;
    }
}
