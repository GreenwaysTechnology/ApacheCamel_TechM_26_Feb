package com.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterOnErrorHandler extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        // ===============================================
        // GLOBAL ERROR HANDLER - applies to ALL routes
        // ===============================================
        errorHandler(deadLetterChannel("direct:failed")
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .backOffMultiplier(2)
                .useExponentialBackOff()
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .log("⚠️ Retry attempt #${header.CamelRedeliveryCounter}"));

        // Route 1: Process payment
        from("direct:payment")
                .log("Starting payment for: ${body}")
                .to("bean:paymentService?method=processPayment")
                .log("Payment completed: ${body}");

        // Route 2: Process order (also uses same errorHandler)
        from("direct:order")
                .log("Processing order: ${body}")
                .process(exchange -> {
                    throw new Exception("Order processing failed");
                });


        // Dead Letter Channel - where failed messages go,right now , i am printing messages in console,
        //later you can log messages into file system or database or into any message brokers
        from("direct:failed")
                .log(LoggingLevel.ERROR, "Message failed: ${body}")
                .log(LoggingLevel.ERROR, "Error: ${exception.message}");

    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=" .repeat(50));
        System.out.println("ERROR HANDLER DEMO");
        System.out.println("=" .repeat(50) + "\n");

        // Test: Payment will succeed after retries
        producerTemplate.sendBody("direct:order", "100");
        System.out.println("\n" + "=" .repeat(50));
        System.out.println("DEMO COMPLETE");
        System.out.println("=" .repeat(50) + "\n");

    }
}
