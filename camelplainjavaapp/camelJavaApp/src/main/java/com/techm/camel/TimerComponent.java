package com.techm.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class TimerComponent {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer:myTimer?period=2000")
                        .setBody()
                        .constant("Hello World!!!")
                        .log("Timer triggered")
                        .to("log:output");
            }
        });

        // 1. Start the context ONCE
        context.start();

        // 2. Keep the main thread alive so the background timer can run
        // In a simple app, we can just sleep for a long time
        Thread.sleep(10000); // Runs for 10 seconds

        // 3. Stop the context gracefully
        context.stop();


    }
}
