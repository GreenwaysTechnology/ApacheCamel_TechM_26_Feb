package com.example;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProcessDemo extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        from("direct:start")
                .process(exchange -> {
                    // Get the current body
                    String body = exchange.getIn().getBody(String.class);
                    // Modify it
                    String modified = body.toUpperCase();
                    // Set the new body
                    exchange.getIn().setBody(modified);
                })
                .to("direct:output");

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
        producerTemplate.sendBody("direct:start","subramanian murugan");
    }
}
