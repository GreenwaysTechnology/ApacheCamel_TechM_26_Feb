package com.example;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/order")
    public ResponseEntity<String> receiveOrder(@RequestBody String orderData) {
        // Send to SEDA queue IMMEDIATELY (non-blocking)
        producerTemplate.asyncSendBody("seda:processQueue", orderData);

        // Return 202 immediately
        return ResponseEntity.accepted().body("Order queued for processing - 202 ACCEPTED");
    }
}
