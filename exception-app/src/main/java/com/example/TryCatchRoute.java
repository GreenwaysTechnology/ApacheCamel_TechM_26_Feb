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
        from("direct:start")
                .routeId("Try--Catch--Route")
                .doTry()
                //any risky code
                .to("direct:risky")
                .log(LoggingLevel.INFO, "Risky Operation is Completed")
                .to("direct:success")
                .doCatch(IOException.class)
                .log(LoggingLevel.ERROR, "IO Exception Occured: ${exception.message}")
                .to("direct:io-error")
                .doCatch(DatabaseException.class)
                .log(LoggingLevel.ERROR, "Database Exception Occured: ${exception.message}")
                .to("direct:db-error")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Database Exception Occured: ${exception.message}")
                .to("direct:general-error")
                .doFinally()
                .log(LoggingLevel.INFO, "Clean Up Operation starts")
                .to("direct:cleanup")
                .end()
                .log("Processed Finished");

        from("direct:risky")
                .routeId("risky-route")
                .log(LoggingLevel.INFO, "Risky Processing logic")
                .to("bean:fileService?method=processFile('data.txt')")
                .log(LoggingLevel.INFO, "File Processing Done,now saving to databse")
                .to("bean:databaseService?method=saveToDatabase");

        from("direct:success")
                .routeId("success-route")
                .log(LoggingLevel.INFO, "All Operations Successfully Completed")
                .log(LoggingLevel.INFO, "Result ${body}");


        from("direct:io-error")
                .routeId("IO-ERROR-route")
                .log(LoggingLevel.ERROR, "File Operation Failed")
                .setBody(constant("IO Error: File operation Failed"))
                .log(LoggingLevel.INFO, "Error Notification sent");

        from("direct:db-error")
                .routeId("DB-ERROR-route")
                .log(LoggingLevel.ERROR, "database Operation Failed")
                .setBody(constant("DB Error: database Operation Failed"))
                .log(LoggingLevel.INFO, "Database Retry scheduled");

        from("direct:general-error")
                .routeId("Gengeral-ERROR-route")
                .log(LoggingLevel.ERROR, "Handling General Errors")
                .setBody(constant("General Error: Operation Failed"))
                .log(LoggingLevel.INFO, "General Exception ");

        from("direct:cleanup")
                .routeId("Cleanup-route")
                .log(LoggingLevel.ERROR, "Handling General Errors")
                .setBody(constant("General Error: Operation Failed"))
                .log(LoggingLevel.INFO, "General Exception ");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running multiple test to simulate exception");
        for (int i = 0; i <= 5; i++) {
            try {
                String result = producerTemplate.requestBody("direct:start", "test-data " + i, String.class);
                System.out.println("Final Result" + result);
            } catch (Exception e) {
                System.out.println("Exception caught in Main " + e.getMessage());
            }
        }
    }
}
