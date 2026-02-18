package com.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


//@Component
public class CustomExceptionChoiceWhen extends RouteBuilder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CustomExceptionUsingProcessMethod.class);

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
                .log(LoggingLevel.ERROR, "Unexpected Error Occurred: ${exception.message}");

        from("direct:processOrder")
                .routeId("Order Validation Route")
                .log(LoggingLevel.INFO, "Received Order for Validation ${body}")
                //validate with custom error messages
                .choice()
                .when(simple("${body} == null"))
                .throwException(ValidationException.class, "Order Cant be Null")
                .when(simple("${body.amount} <= 0 "))
                .throwException(ValidationException.class, "Amount Must be postive")
                .end()
                .log(LoggingLevel.INFO, "Validation Success : ${body}");


    }

    @Override
    public void run(String... args) throws Exception {
        Order order = new Order("ORD-003", "CUST-456", 1000.89, "Smart-Phone");
        //No exception
//        producerTemplate.sendBody("direct:processOrder", order);
        //with order null exception
//        producerTemplate.sendBody("direct:processOrder", null);
        //amount exception
        producerTemplate.sendBody("direct:processOrder", new Order("ORD-003", "CUST-456", -181, "Smart-Phone"));
    }
}
