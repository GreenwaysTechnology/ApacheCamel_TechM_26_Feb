package com.techm.camel;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelMainAnonmous {
    public static void main(String[] args) throws Exception {
        //Create Camel Context
        CamelContext context = new DefaultCamelContext();

        //attach RouteBuilder with context
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                System.out.println("Hello Camel" );
            }
        });

        //start camel runtime
        context.start();

        //terminate the camel runtime
        context.stop();
    }
}
