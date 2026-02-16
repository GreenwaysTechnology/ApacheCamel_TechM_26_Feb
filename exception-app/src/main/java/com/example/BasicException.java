package com.example;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BasicException extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        //Specific
        //General
        onException(Exception.class)
                .handled(true)
                .log("****Exception Caught ${exception.message}")
                .setBody(simple("General Exception :${exception.message}"));

        onException(ArithmeticException.class)
                .handled(true)
                .log("****Arithmetic Exception Caught ${exception.message}")
                .setBody(constant("Error : Division By zero"));

///////////////////////////////////////////////////////////////////////////////////////////////
        from("direct:numbers")
                .log("Starting....")
                .process(exchange -> {
//                     int result = 10 / 0;
                    throw new IllegalArgumentException("Invalid Numbers");
                });



    }

    @Override
    public void run(String... args) throws Exception {
        //producerTemplate.sendBody("direct:numbers", null);
        String result = producerTemplate.requestBody("direct:numbers", "10", String.class);
        System.out.println("Result :" + result);
    }
}
