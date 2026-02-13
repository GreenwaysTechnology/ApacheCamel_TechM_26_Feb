package com.example;

import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

//@Component
public class HeadersDemo extends RouteBuilder implements CommandLineRunner {
    @Autowired
    private ProducerTemplate producerTemplate;


    @Override
    public void configure() throws Exception {
        //Route 1 : sets headers
        //
        from("direct:start")
                .log("Received body: ${body}")
                .setHeader("CustomerId", constant("12345"))
                .setHeader("Priority", simple("${body.priority}"))
                .setHeader("Timestamp", () -> new Date())
                .setHeader("X-Internal-Id", constant("ABC-123"))
                .setHeader("X-Session", constant("SESSION-456"))
                .log("Headers set - CustomerId: ${header.CustomerId}, Priority: ${header.Priority}")
                .to("direct:process");
        // Route 2: Reading headers
        from("direct:process")
                .process(exchange -> {
                    String customerId = exchange.getIn().getHeader("CustomerId", String.class);
                    String priority = exchange.getIn().getHeader("Priority", String.class);
                    Date timestamp = exchange.getIn().getHeader("Timestamp", Date.class);

                    System.out.println("=== Processing Headers ===");
                    System.out.println("Customer ID: " + customerId);
                    System.out.println("Priority: " + priority);
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("========================");
                })
                .to("direct:router");

        // Route 3: Using headers for routing decisions
        from("direct:router")
                .log("Routing based on Priority: ${header.Priority}")
                .choice()
                .when(header("Priority").isEqualTo("HIGH"))
                .log(">>> Routing to URGENT queue")
                .to("direct:urgent")
                .when(header("Priority").isEqualTo("LOW"))
                .log(">>> Routing to NORMAL queue")
                .to("direct:normal")
                .otherwise()
                .log(">>> Routing to DEFAULT queue")
                .to("direct:default")
                .end();

        // Route 4: Urgent processing
        from("direct:urgent")
                .log("URGENT: Processing high priority order for customer ${header.CustomerId}")
                .setHeader("ProcessedBy", constant("UrgentHandler"))
                .to("direct:cleanup");

// Route 5: Normal processing
        from("direct:normal")
                .log("NORMAL: Processing low priority order for customer ${header.CustomerId}")
                .setHeader("ProcessedBy", constant("NormalHandler"))
                .to("direct:cleanup");

        // Route 6: Default processing
        from("direct:default")
                .log("DEFAULT: Processing medium priority order for customer ${header.CustomerId}")
                .setHeader("ProcessedBy", constant("DefaultHandler"))
                .to("direct:cleanup");

        from("direct:cleanup")
                .log("Before cleanup - All headers: ${headers}")
                .removeHeader("SensitiveData")
                .removeHeaders("X-*") // Remove all headers starting with X-
                .log("After cleanup - Remaining headers: ${headers}")
                .to("direct:output");

        // Route 8: Final output
        from("direct:output")
                .process(exchange -> {
                    System.out.println("\n=== Final Output ===");
                    System.out.println("Body: " + exchange.getIn().getBody());
                    System.out.println("Remaining Headers: " + exchange.getIn().getHeaders());
                    System.out.println("===================\n");
                });

    }

    @Override
    public void run(String... args) throws Exception {
//        producerTemplate.sendBody("direct:start", null);
//        producerTemplate.sendBody("direct:start","Hello");
       // producerTemplate.sendBodyAndHeader("direct:start", "Hello-Body", "myheadervalue", "MyHeaderValue");
        // Test 1: High priority order
        System.out.println("\n********** TEST 1: HIGH PRIORITY **********");
        OrderRequest highPriorityOrder = new OrderRequest("ORD-001", "HIGH", 1500.00);
        producerTemplate.sendBody("direct:start", highPriorityOrder);

        // Test 2: Low priority order
        System.out.println("\n********** TEST 2: LOW PRIORITY **********");
        OrderRequest lowPriorityOrder = new OrderRequest("ORD-002", "LOW", 50.00);
        producerTemplate.sendBody("direct:start", lowPriorityOrder);

        // Test 3: Medium priority order (will go to default)
        System.out.println("\n********** TEST 3: MEDIUM PRIORITY **********");
        OrderRequest mediumPriorityOrder = new OrderRequest("ORD-003", "MEDIUM", 500.00);
        producerTemplate.sendBody("direct:start", mediumPriorityOrder);


    }
}