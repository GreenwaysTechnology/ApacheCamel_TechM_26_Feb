package com.example;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRoutes extends RouteBuilder {

    private final RedisProcessor redisProcessor;

    @Override
    public void configure() throws Exception {

        // Global error handler
        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"error\":\"${exception.message}\"}"));

        // REST configuration : we are exposing rest apis
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.off)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/api")  // ✅ must match servlet mapping prefix
                .port(8080);

        // ── REST Endpoints ──
        // http://localhost:8080/api/redis/set
        rest("/redis")
                .post("/set")
                .consumes("application/json").produces("application/json")
                .to("direct:redisSet")

                .get("/get/{key}")
                .produces("application/json")
                .to("direct:redisGet")

                .delete("/delete/{key}")
                .produces("application/json")
                .to("direct:redisDelete")

                .get("/exists/{key}")
                .produces("application/json")
                .to("direct:redisExists")

                .get("/ttl/{key}")
                .produces("application/json")
                .to("direct:redisTtl")

                .post("/lpush")
                .consumes("application/json").produces("application/json")
                .to("direct:redisLpush")

                .get("/lpop/{key}")
                .produces("application/json")
                .to("direct:redisLpop");

        // ── Direct Routes ──

        from("direct:redisSet")
                .unmarshal().json(KeyValueRequest.class)
                .bean(redisProcessor, "set")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:redisGet")
                .bean(redisProcessor, "get")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:redisDelete")
                .bean(redisProcessor, "delete")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:redisExists")
                .bean(redisProcessor, "exists")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:redisTtl")
                .bean(redisProcessor, "ttl")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:redisLpush")
                .unmarshal().json(KeyValueRequest.class)
                .bean(redisProcessor, "lpush")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:redisLpop")
                .bean(redisProcessor, "lpop")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));
    }
}