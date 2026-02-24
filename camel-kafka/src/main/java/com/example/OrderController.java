package com.example;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    // ProducerTemplate is auto-created by Camel Spring Boot
    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody Order order) {
        log.info("REST received order: customerId={}, amount={}",
                order.getCustomerId(), order.getAmount());

        // Send to Camel direct route → Kafka
        producerTemplate.sendBody("direct:publish-order", order);

        return ResponseEntity.accepted().body(Map.of(
                "status", "accepted",
                "orderId", order.getOrderId()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}