package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private int attempts = 0;

    public String processPayment(String amount) throws Exception {
        attempts++;
        log.info("💳 Payment attempt #{} for amount: {}", attempts, amount);

        // Fail first 2 attempts
        if (attempts < 3) {
            throw new Exception("Payment gateway timeout");
        }

        log.info("✅ Payment successful!");
        attempts = 0;
        return "Payment processed: $" + amount;
    }
}
