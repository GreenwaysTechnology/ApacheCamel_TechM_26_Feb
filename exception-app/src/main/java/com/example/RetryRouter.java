package com.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
public class RetryRouter extends RouteBuilder implements CommandLineRunner {
    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        //Define Exception with Retry Policy
        onException(IOException.class)
                .maximumRedeliveries(3)                    // Retry 3 times
                .redeliveryDelay(2000)                     // Wait 2 seconds
                .backOffMultiplier(2)                      // Double wait time each retry
                .useExponentialBackOff()                   // 2s, 4s, 8s
                .retryAttemptedLogLevel(LoggingLevel.WARN) // Log retries as WARNING
                .log(LoggingLevel.WARN, "Retrying... Attempt: ${header.CamelRedeliveryCounter}")
                .handled(true);                            // Handle the exception

        // Simple route
        from("direct:start")
                .log("Starting: ${body}")
                .to("bean:retryService?method=processData")
                .log("Result: ${body}");


    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== Simple Retry Demo ===\n");

        producerTemplate.sendBody("direct:start", "Hello World");

        Thread.sleep(15000); // Wait for retries to complete

        System.out.println("\n=== Demo Complete ===\n");
    }
}
