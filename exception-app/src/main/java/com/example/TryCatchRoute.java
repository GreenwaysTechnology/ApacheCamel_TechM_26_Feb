package com.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TryCatchRoute extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {

        // Main route with doTry/doCatch/doFinally
        from("direct:start")
                .routeId("try-catch-route")
                .log(LoggingLevel.INFO, "=== Starting processing ===")
                .doTry()
                .log(LoggingLevel.INFO, "Attempting risky operation")
                .to("direct:risky")
                .log(LoggingLevel.INFO, "Risky operation completed successfully")
                .to("direct:success")
                .doCatch(IOException.class)
                .log(LoggingLevel.ERROR, "❌ IO Exception occurred: ${exception.message}")
                .to("direct:io-error")
                .doCatch(DatabaseException.class)
                .log(LoggingLevel.ERROR, "❌ Database Exception occurred: ${exception.message}")
                .to("direct:db-error")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "❌ General exception occurred: ${exception.message}")
                .to("direct:general-error")
                .doFinally()
                .log(LoggingLevel.INFO, "🧹 Cleanup operations starting")
                .to("direct:cleanup")
                .log(LoggingLevel.INFO, "🧹 Cleanup operations completed")
                .end()
                .log(LoggingLevel.INFO, "=== Processing finished ===");

        // Risky operation route
        from("direct:risky")
                .routeId("risky-route")
                .log(LoggingLevel.INFO, "Processing risky business logic")
                .to("bean:fileService?method=processFile('data.txt')")
                .log(LoggingLevel.INFO, "File processing done, now saving to database")
                .to("bean:databaseService?method=saveToDatabase");

        // Success handler
        from("direct:success")
                .routeId("success-route")
                .log(LoggingLevel.INFO, "✅ SUCCESS: All operations completed successfully")
                .log(LoggingLevel.INFO, "Result: ${body}");

        // IO Error handler
        from("direct:io-error")
                .routeId("io-error-route")
                .log(LoggingLevel.ERROR, "Handling IO error")
                .setBody(constant("IO Error: File operation failed"))
                .log(LoggingLevel.INFO, "Error notification sent to admin");

        // Database Error handler
        from("direct:db-error")
                .routeId("db-error-route")
                .log(LoggingLevel.ERROR, "Handling Database error")
                .setBody(constant("DB Error: Database operation failed"))
                .log(LoggingLevel.INFO, "Database retry scheduled");

        // General Error handler
        from("direct:general-error")
                .routeId("general-error-route")
                .log(LoggingLevel.ERROR, "Handling general error")
                .setBody(constant("General Error: Operation failed"))
                .log(LoggingLevel.INFO, "Support ticket created");

        // Cleanup route
        from("direct:cleanup")
                .routeId("cleanup-route")
                .log(LoggingLevel.INFO, "Performing cleanup tasks")
                .to("bean:databaseService?method=cleanup")
                .log(LoggingLevel.INFO, "Resources released");

    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("CAMEL TRY-CATCH-FINALLY DEMO");
        System.out.println("=".repeat(70));
        // Run multiple tests to see different scenarios
        for (int i = 1; i <= 5; i++) {
            System.out.println("\n" + "-".repeat(70));
            System.out.println("TEST RUN #" + i);
            System.out.println("-".repeat(70));

            try {
                String result = producerTemplate.requestBody("direct:start", "Test Data " + i, String.class);
                System.out.println("Final Result: " + result);
            } catch (Exception e) {
                System.out.println("Exception caught in main: " + e.getMessage());
            }

            Thread.sleep(1000);
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("DEMO COMPLETED");
        System.out.println("=".repeat(70));

    }

}
