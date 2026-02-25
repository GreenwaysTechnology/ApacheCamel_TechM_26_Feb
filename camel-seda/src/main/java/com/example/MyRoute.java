package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {
    @Override
    public void configure() {

        from("seda:processQueue?concurrentConsumers=5&size=100")
                .routeId("order-processor")
                .log("Processing order: ${body}")
                .delay(2000) // Simulate heavy work (2 seconds)
                .setBody(simple("${body} processed at ${date:now:yyyy-MM-dd HH:mm:ss}"))
                .log("Order processing complete: Thread Name : ${threadName} Body: ${body}");
    }
}
