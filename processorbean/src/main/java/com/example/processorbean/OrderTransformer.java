package com.example.processorbean;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderTransformer {
    public String toDownStreamFormat(@Body Order order, @Headers Map<String, Object> headers) {
        return String.format(
                "ORDER_ID=%s | CUSTOMER=%s | AMOUNT=%s | DISCOUNT=%.0f%% | STATUS=%s | SOURCE=%s ",
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                order.getDiscount(),
                order.getDiscount() * 10,
                order.getStatus(),
                headers.getOrDefault("orderSource", "UNKNOWN")

        );
    }
}
