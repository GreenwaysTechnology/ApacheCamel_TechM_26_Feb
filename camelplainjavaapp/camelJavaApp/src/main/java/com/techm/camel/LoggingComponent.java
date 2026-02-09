package com.techm.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class LoggingComponent {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        //log is component; incoming; end point;?variableName=value is query string
                        .to("log:incoming?level=INFO&showBody=true")
                        .to("log:headers?level=INFO&showHeaders=true")
                        .to("log:exchange?level=INFO&showExchangeId=true")
                        .to("mock:end");
            }
        });

        context.start();
        //ProducerTemplate is object used to send message to direct:start
        //sending message from outside of camel datasources
        ProducerTemplate producerTemplate = context.createProducerTemplate();
//        producerTemplate.sendBody("direct:start","Hello Camel");
        producerTemplate.sendBodyAndHeader("direct:start", "This is body", "myheader", "HelloCamel");


        // Keep app alive
        Thread.sleep(3000);

        context.stop();
    }
}
