package com.example;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OrderConsumerRoute extends RouteBuilder {

    private final OrderProcessor orderProcessor;
    private final AuditProcessor auditProcessor;

    @Value("${app.kafka.brokers}")
    private String brokers;
    @Value("${app.kafka.topics.orders}")
    private String ordersTopic;
    @Value("${app.kafka.topics.orders-dlq}")
    private String dlqTopic;
    @Value("${app.kafka.topics.payments}")
    private String paymentsTopic;
    @Value("${app.kafka.topics.notifications}")
    private String notifTopic;
    @Value("${app.kafka.topics.audit}")
    private String auditTopic;
    @Value("${app.kafka.consumer.group-id}")
    private String groupId;
    @Value("${app.kafka.consumer.consumers-count}")
    private int consumersCount;
    @Value("${app.kafka.consumer.max-poll-records}")
    private int maxPollRecords;

    @Override
    public void configure() {

        // ── 1. Error handler → direct:dlq-handler (not directly to Kafka) ────
        errorHandler(
                deadLetterChannel("direct:dlq-handler")
                        .maximumRedeliveries(3)
                        .redeliveryDelay(1_000)
                        .backOffMultiplier(2)
                        .useExponentialBackOff()
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );

        // ── 2. Main consumer ─────────────────────────────────────────────────
        from(consumerUri(ordersTopic))
                .routeId("order-consumer-route")
                .log(LoggingLevel.INFO,
                        "Received: topic=${header.kafka.TOPIC}, " +
                                "partition=${header.kafka.PARTITION}, offset=${header.kafka.OFFSET}")
                .unmarshal().json(JsonLibrary.Jackson, Order.class)
                .to("bean-validator:validateOrder")
                .process(orderProcessor)
                .multicast().parallelProcessing()
                .to("direct:to-payments", "direct:to-notifications")
                .end()
                .process(auditProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:" + auditTopic + "?brokers=" + brokers)
                .process(this::commitOffset)
                .log(LoggingLevel.INFO, "Order done: ${header.orderId}");

        // ── 3. Payments fan-out ───────────────────────────────────────────────
        from("direct:to-payments")
                .routeId("to-payments-route")
                .setHeader(KafkaConstants.KEY, header("orderId"))
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:" + paymentsTopic + "?brokers=" + brokers)
                .log(LoggingLevel.DEBUG, "→ payments: ${header.orderId}");

        // ── 4. Notifications fan-out ──────────────────────────────────────────
        from("direct:to-notifications")
                .routeId("to-notifications-route")
                .setBody(header("notification"))
                .setHeader(KafkaConstants.KEY, header("customerId"))
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:" + notifTopic + "?brokers=" + brokers)
                .log(LoggingLevel.DEBUG, "→ notifications: ${header.customerId}");

        // ── 5. DLQ handler: properly serialize then write to Kafka DLQ ────────
        from("direct:dlq-handler")
                .routeId("dlq-handler-route")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(
                            Exchange.EXCEPTION_CAUGHT, Exception.class);
                    Object body = exchange.getIn().getBody();
                    Order order;
                    if (body instanceof Order o) {
                        order = o;
                    } else {
                        order = new Order();
                    }
                    order.setStatus(Order.OrderStatus.FAILED);
                    order.setErrorMessage(cause != null ? cause.getMessage() : "Unknown error");
                    exchange.getIn().setBody(order);
                    log.error("Sending to DLQ: orderId={}, reason={}",
                            order.getOrderId(), order.getErrorMessage());
                })
                .marshal().json(JsonLibrary.Jackson)   // ← always JSON before Kafka
                .to("kafka:" + dlqTopic + "?brokers=" + brokers);

        // ── 6. DLQ monitor: safe, won't loop ──────────────────────────────────
        from(consumerUri(dlqTopic) + "&groupId=" + groupId + "-dlq")
                .routeId("dlq-monitor-route")
                .log(LoggingLevel.ERROR,
                        "DLQ message: topic=${header.kafka.TOPIC}, offset=${header.kafka.OFFSET}")
                .doTry()
                .unmarshal().json(JsonLibrary.Jackson, Order.class)
                .log(LoggingLevel.ERROR,
                        "Failed order → id=${body.orderId}, reason=${body.errorMessage}")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR,
                        "DLQ raw (could not parse): ${body}")
                .endDoTry()
                .process(this::commitOffset);  // ← always commit, never loop
    }

    private String consumerUri(String topic) {
        return "kafka:" + topic
                + "?brokers=" + brokers
                + "&groupId=" + groupId
                + "&autoCommitEnable=false"
                + "&allowManualCommit=true"
                + "&consumersCount=" + consumersCount
                + "&maxPollRecords=" + maxPollRecords
                + "&autoOffsetReset=earliest";
    }

    private void commitOffset(Exchange exchange) {
        KafkaManualCommit manual = exchange.getIn()
                .getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
        if (manual != null) {
            manual.commit();
        }
    }
}