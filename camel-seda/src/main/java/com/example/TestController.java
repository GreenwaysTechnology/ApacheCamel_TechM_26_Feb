package com.example;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/orders/{count}")
    public String sendMultipleOrders(@PathVariable int count) {
        for (int i = 1; i <= count; i++) {
            producerTemplate.asyncSendBody(
                    "seda:orderProcessing",
                    "Order #" + i + ": Laptop x" + i
            );
        }
        return "🚀 Sent " + count + " orders to SEDA queue";
    }
}
