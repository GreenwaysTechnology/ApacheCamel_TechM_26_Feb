package com.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderProducerRoute extends RouteBuilder {

    @Value("${app.kafka.brokers}")
    private String brokers;

    @Value("${app.kafka.topics.orders}")
    private String ordersTopic;

    @Override
    public void configure() {

        // Receives Order object from OrderController
        from("direct:publish-order")
                .routeId("order-producer-route")
                .setHeader(KafkaConstants.KEY, simple("${body.orderId}"))
                .log(LoggingLevel.INFO, "Publishing to Kafka: orderId=${body.orderId}")
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:" + ordersTopic + "?brokers=" + brokers)
                .log(LoggingLevel.INFO, "Published: ${header.kafka.KEY}");
    }
}


