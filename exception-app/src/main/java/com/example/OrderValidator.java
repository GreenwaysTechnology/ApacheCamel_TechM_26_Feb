package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("validator")
public class OrderValidator {
    private static final Logger log = LoggerFactory.getLogger(OrderValidator.class);
    public void validate(Order order) throws ValidationException {
        log.info("Validating order: {}", order);

        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }

        if (order.getCustomerId() == null || order.getCustomerId().trim().isEmpty()) {
            throw new ValidationException("Customer ID is required");
        }

        if (order.getAmount() <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        if (order.getProduct() == null || order.getProduct().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }

        log.info("Validation successful for order: {}", order.getId());
    }

}
