package com.example;


import org.apache.camel.component.kafka.KafkaComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.brokers}")
    private String brokers;

    @Value("${app.kafka.consumer.group-id}")
    private String groupId;

    @Value("${app.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${app.kafka.consumer.consumers-count}")
    private int consumersCount;

    @Value("${app.kafka.producer.retries}")
    private int retries;

    @Value("${app.kafka.producer.acks}")
    private String acks;

    @Bean
    public KafkaComponent kafka() {
        KafkaComponent kafka = new KafkaComponent();
        kafka.getConfiguration().setBrokers(brokers);

        // Consumer defaults
        kafka.getConfiguration().setGroupId(groupId);
        kafka.getConfiguration().setAutoOffsetReset(autoOffsetReset);
        kafka.getConfiguration().setConsumersCount(consumersCount);
        kafka.getConfiguration().setAutoCommitEnable(false);  // manual commit

        // Producer defaults
        kafka.getConfiguration().setRequestRequiredAcks(acks);
        kafka.getConfiguration().setRetries(retries);

        return kafka;
    }
}