package com.example;


import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisProcessor {

    private final StringRedisTemplate redisTemplate;

    public void set(Exchange exchange) {
        KeyValueRequest req = exchange.getIn().getBody(KeyValueRequest.class);
        if (req.getTtl() != null) {
            redisTemplate.opsForValue().set(req.getKey(), req.getValue(), Duration.ofSeconds(req.getTtl()));
        } else {
            redisTemplate.opsForValue().set(req.getKey(), req.getValue());
        }
        exchange.getIn().setBody("{\"status\":\"OK\",\"key\":\"" + req.getKey() + "\"}");
    }

    public void get(Exchange exchange) {
        String key = exchange.getIn().getHeader("key", String.class);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody("{\"error\":\"Key not found\"}");
        } else {
            exchange.getIn().setBody("{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}");
        }
    }

    public void delete(Exchange exchange) {
        String key = exchange.getIn().getHeader("key", String.class);
        redisTemplate.delete(key);
        exchange.getIn().setBody("{\"status\":\"deleted\",\"key\":\"" + key + "\"}");
    }

    public void exists(Exchange exchange) {
        String key = exchange.getIn().getHeader("key", String.class);
        Boolean exists = redisTemplate.hasKey(key);
        exchange.getIn().setBody("{\"key\":\"" + key + "\",\"exists\":" + exists + "}");
    }

    public void lpush(Exchange exchange) {
        KeyValueRequest req = exchange.getIn().getBody(KeyValueRequest.class);
        redisTemplate.opsForList().leftPush(req.getKey(), req.getValue());
        exchange.getIn().setBody("{\"status\":\"pushed\",\"key\":\"" + req.getKey() + "\"}");
    }

    public void lpop(Exchange exchange) {
        String key = exchange.getIn().getHeader("key", String.class);
        String value = redisTemplate.opsForList().leftPop(key);
        if (value == null) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody("{\"error\":\"List empty or key not found\"}");
        } else {
            exchange.getIn().setBody("{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}");
        }
    }

    public void ttl(Exchange exchange) {
        String key = exchange.getIn().getHeader("key", String.class);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        exchange.getIn().setBody("{\"key\":\"" + key + "\",\"ttlSeconds\":" + ttl + "}");
    }
}