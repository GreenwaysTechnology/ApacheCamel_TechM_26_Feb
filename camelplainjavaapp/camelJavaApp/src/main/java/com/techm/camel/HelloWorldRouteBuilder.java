package com.techm.camel;

import org.apache.camel.builder.RouteBuilder;

//where we write actual integration,processing logic
public class HelloWorldRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //here we write data integration logic
        System.out.println("Hello Camel!");
    }
}
