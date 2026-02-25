package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MultiOrderProcessor extends RouteBuilder {

    @Override
    public void configure() {
        // Route consuming from SEDA queue
        from("seda:orderProcessing?concurrentConsumers=5&size=100")
                .routeId("multi-order-processor")
                .log("🔄 [Thread: ${threadName}] Processing order: ${body}")

                // Simulate heavy processing (DB call, validation, etc.)
                .delay(simple("${random(1000,3000)}")) // 1-3 sec random delay

                // Business logic
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    String processed = body + " | Processed at: " + java.time.LocalDateTime.now();
                    exchange.getIn().setBody(processed);
                })

                // Final output (mock DB, email, etc.)
//                .to("mock:orderRepository")
                .log("✅ [Thread: ${threadName}] Order COMPLETED: ${body}");
    }
}
