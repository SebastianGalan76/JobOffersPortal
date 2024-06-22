package com.coresaken.jobportal.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@WebFilter(urlPatterns = "/*")
public class RateLimitingFilter implements Filter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String ip = httpServletRequest.getRemoteAddr();
        String requestURI = httpServletRequest.getRequestURI();

        if (!requestURI.equals("/too-many-redirects") && !requestURI.endsWith(".css") && !requestURI.endsWith(".svg") && !requestURI.endsWith(".png") && !requestURI.endsWith(".jpg")) {
            Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);
            
            if (!bucket.tryConsume(1)) {
                httpServletResponse.sendRedirect("/too-many-redirects");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private Bucket newBucket(String key) {
        Refill refill = Refill.greedy(10, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(1000, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }
}