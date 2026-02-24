package com.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumerRoute extends RouteBuilder {

    @Value("${app.kafka.brokers}")
    private String brokers;
    @Value("${app.kafka.topics.notifications}")
    private String notifTopic;
    @Value("${app.kafka.consumer.group-id}")
    private String groupId;

    @Override
    public void configure() {

        // Retry twice, then log and move on
        errorHandler(defaultErrorHandler()
                .maximumRedeliveries(2)
                .redeliveryDelay(500)
                .retryAttemptedLogLevel(LoggingLevel.WARN));

        from("kafka:" + notifTopic
                + "?brokers=" + brokers
                + "&groupId=" + groupId + "-notifications"
                + "&autoCommitEnable=false"
                + "&allowManualCommit=true"
                + "&autoOffsetReset=earliest")
                .routeId("notification-consumer-route")
                .unmarshal().json(JsonLibrary.Jackson, Notification.class)
                .log(LoggingLevel.INFO,
                        "Notification: type=${body.type}, customer=${body.customerId}")

                // Content-based routing by notification type
                .choice()
                .when(simple("${body.type} == 'ORDER_CONFIRMED'"))
                .to("direct:send-email")
                .when(simple("${body.type} == 'PAYMENT_PROCESSED'"))
                .to("direct:send-sms")
                .when(simple("${body.type} == 'ORDER_FAILED'"))
                .to("direct:send-alert")
                .otherwise()
                .log(LoggingLevel.WARN, "Unknown type: ${body.type}")
                .end()
                .process(this::commitOffset);

        from("direct:send-email")
                .routeId("email-route")
                // Replace with: .to("smtp://smtp.example.com?...")
                .log(LoggingLevel.INFO,
                        "📧 EMAIL → ${body.customerId}: ${body.message}");

        from("direct:send-sms")
                .routeId("sms-route")
                // Replace with: .to("twilio:...")
                .log(LoggingLevel.INFO,
                        "📱 SMS → ${body.customerId}: ${body.message}");

        from("direct:send-alert")
                .routeId("alert-route")
                .log(LoggingLevel.ERROR,
                        "🚨 ALERT → order ${body.orderId} FAILED");
    }

    private void commitOffset(Exchange exchange) {
        KafkaManualCommit manual = exchange.getIn()
                .getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
        if (manual != null) {
            manual.commit();
        }
    }
}