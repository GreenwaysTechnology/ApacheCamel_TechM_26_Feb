package com.example;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
public class OrderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Order order = exchange.getIn().getBody(Order.class);

        log.info("Processing order: orderId={}, amount={}",
                order.getOrderId(), order.getAmount());

        // Business rule: reject orders above $10,000
        if (order.getAmount().doubleValue() > 10_000) {
            throw new IllegalArgumentException(
                    "Order amount exceeds limit: " + order.getAmount());
        }

        // Update status
        order.setStatus(Order.OrderStatus.PROCESSING);

        // Build notification for the notifications topic
        Notification notification = Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .message("Your order " + order.getOrderId() + " is being processed.")
                .type(Notification.NotificationType.ORDER_CONFIRMED)
                .build();

        // Pass data to downstream routes via headers
        exchange.getIn().setBody(order);
        exchange.getIn().setHeader("notification", notification);
        exchange.getIn().setHeader("orderId",     order.getOrderId());
        exchange.getIn().setHeader("customerId",  order.getCustomerId());
    }
}
