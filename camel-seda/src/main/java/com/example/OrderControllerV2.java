package com.example;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v2")
public class OrderControllerV2 {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/order")
    public ResponseEntity<String> receiveOrder(@RequestBody String orderData) {
        // Send to SEDA queue IMMEDIATELY (fire-and-forget)
        producerTemplate.asyncSendBody(
                "seda:orderProcessing?size=100",
                orderData + " | Received at: " + LocalDateTime.now()
        );

        return ResponseEntity.accepted()
                .body("✅ Order queued for async processing (202 ACCEPTED)");
    }
}
