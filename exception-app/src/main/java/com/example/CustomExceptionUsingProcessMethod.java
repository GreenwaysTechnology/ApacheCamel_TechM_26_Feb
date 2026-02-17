package com.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class CustomExceptionUsingProcessMethod extends RouteBuilder implements CommandLineRunner {
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
                .log(LoggingLevel.ERROR, "Unexpected Error Occured: ${exception.message}");

        //biz logic with process method
        from("direct:processOrder")
                .process(exchange -> {
                    //you have write biz logic and throw exception
                    Order order = exchange.getIn().getBody(Order.class);
                    if (order == null) {
                        throw new ValidationException("Order Cant be null");
                    }
                    if (order.getAmount() <= 0) {
                        throw new ValidationException("Amount must be positive");
                    }
                    log.info("Validation passed for order: {}", order);
                })
                .log("Processing Completed Successfully");


    }

    @Override
    public void run(String... args) throws Exception {
        Order order = new Order("ORD-003", "CUST-456", 345, "Smart-Phone");
        //No exception
//        producerTemplate.sendBody("direct:processOrder", order);
        //with order null exception
         producerTemplate.sendBody("direct:processOrder", null);
        //amount exception
//        producerTemplate.sendBody("direct:processOrder", new Order("ORD-003", "CUST-456", -181, "Smart-Phone"));
    }
}
