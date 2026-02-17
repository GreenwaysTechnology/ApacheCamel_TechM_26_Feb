package com.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class CustomExceptionUsingBean extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        //sepcific exception
        onException(ValidationException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Validation Failed : ${exception.message}")
                .log(LoggingLevel.ERROR, "Failed Order Details ${body}")
                .log(LoggingLevel.INFO, "Order is Rejected and logged");

        //General Exception
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Unexpected Error Occured: ${exception.message}");

        //biz logic with bean(spring bean -class-Object)
        from("direct:processOrder")
                .routeId("order-processing-route")
                .log(LoggingLevel.INFO, "Received Order: ${body}")
                .to("bean:validator") //bean is component,validator is beanName
                .log(LoggingLevel.INFO, "Order validated Successfully")
                .log(LoggingLevel.INFO, "Order Processed: ${body.id}")
                .log(LoggingLevel.INFO, "Processing completed for Customer: ${body.customerId}");


    }

    @Override
    public void run(String... args) throws Exception {
        Order order = new Order("ORD-003", "CUST-456", 345, "Smart-Phone");
        //No exception
        //producerTemplate.sendBody("direct:processOrder", order);
        //with order null exception
        // producerTemplate.sendBody("direct:processOrder", null);
        //amount exception
        producerTemplate.sendBody("direct:processOrder", new Order("ORD-003", "CUST-456", -181, "Smart-Phone"));
    }
}
