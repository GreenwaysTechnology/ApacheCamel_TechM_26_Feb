package com.example.processorbean;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    public Order applyDiscount(@Body Order order, @Header("orderSource") String source) {
        double discount = 0.0;
        if (order.getAmount() > 1000) {
            discount = 0.10;
        } else if ("PREMIUM".equals(source)) {
            discount = 0.05;
        }
        order.setDiscount(discount);
        order.setAmount(order.getAmount() * (1 - discount));
        order.setStatus("DISCOUNTED");
        return order;
    }
}
