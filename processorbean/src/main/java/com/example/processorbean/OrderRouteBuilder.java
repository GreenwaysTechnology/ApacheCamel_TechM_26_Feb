package com.example.processorbean;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderRouteBuilder extends RouteBuilder {

    @Autowired
    private OrderValidationProcessor validationProcessor;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private OrderTransformer orderTransformer;


    @Override
    public void configure() throws Exception {
        from("direct:orders")
                .log(">>Received Order: ${body}")
                .process(validationProcessor)
                .choice()
                .when(header("validationStatus").isEqualTo("INVALID"))
                .log(">>>Invalid Order: {header.validationError}")
                .to("direct:deadLetterOrders")
                .otherwise()
                .log(">>>Order is Valid,applying Discount...")
                //Bean-Pure biz logic
                .bean(discountService, "applyDiscount")
                .log(">>>After Discount:${body}")
                //Bean - Transfomer
                .bean(orderTransformer, "toDownStreamFormat")
                .log(">>>Final Output:${body}")
                .to("direct:processOrders")
                .end();

        from("direct:processOrders")
                .log("Success----Processed: ${body}");

        from("direct:deadLetterOrders")
                .log("Failed----DeadLetter:Reason : ${header.validationError}");

    }

    @Bean
    CommandLineRunner run(ProducerTemplate template) {
        return args -> {
//            System.out.println("\n========== TEST 1: Valid Large Order ==========");
//            Order order1 = new Order("ORD-001", "CUST-A", 2000.0, null, 0);
//            Map<String, Object> headers1 = new HashMap<>();
//            headers1.put("orderSource", "WEB");
//            template.sendBodyAndHeaders("direct:orders", order1, headers1);
//            System.out.println("\n========== TEST 2: Premium Small Order ==========");
//            Order order2 = new Order("ORD-002", "CUST-B", 500.0, null, 0);
//            Map<String, Object> headers2 = new HashMap<>();
//            headers2.put("orderSource", "PREMIUM");
//            template.sendBodyAndHeaders("direct:orders", order2, headers2);
//            System.out.println("\n========== TEST 3: No Discount Order ==========");
//            Order order3 = new Order("ORD-003", "CUST-C", 300.0, null, 0);
//            Map<String, Object> headers3 = new HashMap<>();
//            headers3.put("orderSource", "WEB");
//            template.sendBodyAndHeaders("direct:orders", order3, headers3);

//            System.out.println("\n========== TEST 4: Invalid Order (missing ID) ==========");
//            Order order4 = new Order(null, "CUST-D", 100.0, null, 0);
//            template.sendBody("direct:orders", order4);
            System.out.println("\n========== TEST 5: Invalid Order (zero amount) ==========");
            Order order5 = new Order("ORD-005", "CUST-E", -1, null, 0);
            template.sendBody("direct:orders", order5);


        };
    }
}
