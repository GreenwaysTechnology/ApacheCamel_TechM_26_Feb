package com.example;

import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
public class ExchangeDemo extends RouteBuilder implements CommandLineRunner {
    @Autowired
    private ProducerTemplate producerTemplate;


    @Override
    public void configure() throws Exception {
        from("direct:start")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    String body = in.getBody(String.class);
                    Map<String, Object> headers = in.getHeaders();
                    System.out.println("Body: " + body);
                    System.out.println("Headers" + headers);
                    //set header
                    in.setHeader("name", "Subramanian");
                    in.setMessageId("myMessageId");
                    exchange.setMessage(in);
                })
                .process(exchange -> {
                    Message in = exchange.getIn();
                    String body = in.getBody(String.class);
                    Map<String, Object> headers = in.getHeaders();
                    System.out.println("Body: " + body);
                    System.out.println("Headers" + headers);
                });
                //.end();


    }

    @Override
    public void run(String... args) throws Exception {
//        producerTemplate.sendBody("direct:start", null);
//        producerTemplate.sendBody("direct:start","Hello");
        producerTemplate.sendBodyAndHeader("direct:start", "Hello-Body", "myheadervalue", "MyHeaderValue");
    }
}
