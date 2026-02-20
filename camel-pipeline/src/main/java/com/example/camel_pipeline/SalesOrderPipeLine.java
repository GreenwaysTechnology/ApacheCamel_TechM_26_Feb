package com.example.camel_pipeline;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SalesOrderPipeLine extends RouteBuilder {

    public static class BatchAggregationStrategy implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            String newBody = newExchange.getIn().getBody(String.class);
            if (oldExchange == null) {
                return newExchange;
            }
            String oldBody = oldExchange.getIn().getBody(String.class);
            oldExchange.getIn().setBody(oldBody + "\n" + newBody);
            return oldExchange;
        }
    }

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("file:output/failed?autoCreate=true")
                .maximumRedeliveries(0)
                .logExhausted(true)
                .logStackTrace(true));

        onException(Exception.class)
                .handled(true)
                .log("EXCEPTION CAUGHT: ${exception.message}")
                .setHeader("CamelFileName", simple("error-${date:now:yyyyMMdd-HHmmss}.txt"))
                .to("file:output/failed?autoCreate=true&fileExist=Override");

        from("file:input/orders?noop=true&delay=2000&autoCreate=true")
                // ── Convert body to String immediately ─────────────────────────
                // File component gives a GenericFile object by default
                // convertBodyTo(String.class) forces it to read file contents as text
                .convertBodyTo(String.class)
                .log(">>> STEP 1 FILE READ: ${header.CamelFileName}")
                .log(">>> BODY PREVIEW: ${body}")
                .choice()
                .when(simple("${body} contains 'CORRUPT'"))
                .log(">>> STEP 2 CORRUPT FILE DETECTED: ${header.CamelFileName}")
                .setHeader("CamelFileName", simple("failed-${header.CamelFileName}"))
                .to("file:output/failed?autoCreate=true&fileExist=Override")
                .log(">>> CORRUPT FILE SAVED TO FAILED FOLDER")
                .otherwise()
                .log(">>> STEP 2 VALID FILE: ${header.CamelFileName}")
                .transform(simple("PROCESSED@${date:now:HH:mm:ss}\n${body}"))
                .log(">>> STEP 3 TRANSFORM DONE")
                .split(body().tokenize("\n"))
                .filter(simple("${body.trim()} != ''"))
                .aggregate(constant("batch"), new BatchAggregationStrategy())
                .completionSize(2)
                .completionTimeout(10000)
                .log(">>> STEP 5 BATCH COMPLETE — writing file")
                .setHeader("CamelFileName", simple("batch-${date:now:yyyyMMdd-HHmmss}.txt"))
                .to("log:OrderBatch?showBody=true")
                .to("file:output/batches?autoCreate=true&fileExist=Override")
                .log(">>> BATCH WRITTEN: ${header.CamelFileName}")
                .end()
                .end()
                .endChoice();



    }
}
