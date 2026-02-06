package com.techm.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelMain {
    public static void main(String[] args) throws Exception {
        //Create Camel Context
        CamelContext context = new DefaultCamelContext();

        //attach RouteBuilder with context
        context.addRoutes(new HelloWorldRouteBuilder());

        //start camel runtime
        context.start();

        //terminate the camel runtime
        context.stop();
    }
}
