package com.example;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuditProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Order order = exchange.getIn().getBody(Order.class);

        // Read Kafka metadata from headers
        String topic     = exchange.getIn().getHeader(KafkaConstants.TOPIC,     String.class);
        String partition = String.valueOf(
                exchange.getIn().getHeader(KafkaConstants.PARTITION, Integer.class));
        Long   offset    = exchange.getIn().getHeader(KafkaConstants.OFFSET,    Long.class);

        Map<String, Object> audit = new HashMap<>();
        audit.put("orderId",     order != null ? order.getOrderId() : "unknown");
        audit.put("status",      order != null ? order.getStatus()  : "unknown");
        audit.put("sourceTopic", topic);
        audit.put("partition",   partition);
        audit.put("offset",      offset);
        audit.put("auditTime",   LocalDateTime.now().toString());
        audit.put("exchangeId",  exchange.getExchangeId());

        exchange.getIn().setBody(audit);
    }
}
