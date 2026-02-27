package com.example;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerRoute extends RouteBuilder {

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Override
    public void configure() throws Exception {

        //api/orders/123
        //declare rest api
        rest("/orders")
                .get("/{id}")
                .to("direct:getOrder");

        from("direct:getOrder")
                .routeId("order-circuit-breaker-route")
                .log("Received request for order ID: ${header.id}")

                // ✅ COMPLETE CIRCUIT BREAKER BLOCK
                .circuitBreaker()
                .resilience4jConfiguration()
                .failureRateThreshold(50)
                .slidingWindowSize(5)
                .waitDurationInOpenState(10)
                .end()  // End resilience4j config

                // ✅ PROTECTED CALLS
                .log("Calling external service...")
                .toD(externalServiceUrl + "?orderId=${header.id}&bridgeEndpoint=true")
                .log("External service responded: ${body}")

                // ✅ FALLBACK MUST BE HERE (inside circuitBreaker)
                .onFallback()
                .log("FALLBACK triggered")
                .setBody(simple("""
                            {
                              "orderId": "${header.id}",
                              "status": "FALLBACK",
                              "message": "Service temporarily unavailable"
                            }
                            """))
                .setHeader("Content-Type", constant("application/json"))
                .end()  // End onFallback

                .end()  // End circuitBreaker

                .log("Route completed for order: ${header.id}");
    }
}


