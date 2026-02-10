package com.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TimerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //Print all messages
//        from("timer:hello?period=2000")
//                .setBody()
//                .constant("Hello World from Spring Boot!!!")
//                .to("log:output?showAll=true");

        from("timer:hello?period=2000&repeatCount=5")
                .routeId("myTimerRoute") // Best practice: Always name your routes
                .setBody().constant("Hello World from Spring Boot!!!")
                .to("log:output?showAll=true");


    }
}
