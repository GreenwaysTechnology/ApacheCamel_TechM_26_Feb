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
public class CustomExceptionUsingBuiltInValidator extends RouteBuilder implements CommandLineRunner {
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

        //biz logic with Validator
        Predicate bodyNotNull = simple("${body} != null");
        Predicate amountPositive = simple("${body.amount} > 0");
        //you can write more biz logic
        from("direct:processOrder")
                .routeId("Order-Validation-Route")
                .log(LoggingLevel.INFO, "Received Order for validation ${body}")
                //Validate
                .validate(bodyNotNull)
                .log(LoggingLevel.DEBUG, "Order is not null")
                .validate(amountPositive)
                .log(LoggingLevel.DEBUG, "Order Amount must be postive")
                //All validations passed
                .log(LoggingLevel.INFO, "Validation Successfull:${body}")
                .log(LoggingLevel.INFO, "Processing Order ${body.id}");


    }

    @Override
    public void run(String... args) throws Exception {
        Order order = new Order("ORD-003", "CUST-456", 345, "Smart-Phone");
        //No exception
//        producerTemplate.sendBody("direct:processOrder", order);
        //with order null exception
//        producerTemplate.sendBody("direct:processOrder", null);
        //amount exception
        producerTemplate.sendBody("direct:processOrder", new Order("ORD-003", "CUST-456", -181, "Smart-Phone"));
    }
}
