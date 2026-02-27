package com.example;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class UnstableServiceController {

    private final AtomicInteger callCount = new AtomicInteger(0);

    /**
     * Simulates an unstable service:
     * - Fails on every 2nd call to trigger circuit breaker
     */
    @GetMapping("/unstable-service")
    public Map<String, Object> unstableService(@RequestParam String orderId) {
        int count = callCount.incrementAndGet();

        //cb failure simulation
        if (count % 2 == 0) {
            throw new RuntimeException("Simulated service failure on call #" + count);
        }

        return Map.of(
                "orderId", orderId,
                "status", "SUCCESS",
                "product", "Laptop",
                "callCount", count
        );
    }
}