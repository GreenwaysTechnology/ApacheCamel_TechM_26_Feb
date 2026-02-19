package com.example.processorbean;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class OrderValidationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Order order = exchange.getIn().getBody(Order.class);
        String source = exchange.getIn().getHeader("order-source", String.class);
        if (order == null || order.getId() == null) {
            System.out.println("Order is Null.....");
            exchange.getIn().setHeader("validationStatus", "INVALID");
            exchange.getIn().setHeader("validationError", "Order Id is Missing");
            return;
        }
        if (order.getAmount() <= 0.0) {
            System.out.println("Order amount is invalid.....");
            exchange.getIn().setHeader("validationStatus", "INVALID");
            exchange.getIn().setHeader("validationError", "Amount must be postitive");
            return;
        }
        exchange.getIn().setHeader("validationStatus", "VALID");
        exchange.getIn().setHeader("orderSource", source != null ? source : "UNKNOWN");
    }
}
