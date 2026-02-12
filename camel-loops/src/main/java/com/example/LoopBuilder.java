package com.example;

import jakarta.annotation.PostConstruct;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LoopBuilder extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate template;


    @Override
    public void configure() throws Exception {
        //loop
//        from("direct:start")
//                .loop(5)
//                .log("Loop Count :${exchangeProperty.CamelLoopIndex}")
//                .end()
//                .log("Processing Completed");

        //do...while
//        from("direct:start")
//                .setHeader("counter",constant(0))
//                .loopDoWhile(simple("${header.counter} < 3"))
//                .log("Counter => ${header.counter}")
//                .process(e->{
//                    int c = e.getIn().getHeader("counter",Integer.class);
//                    e.getIn().setHeader("counter",c+1);
//                })
//                .end();

//        from("direct:start")
//                .setBody(constant("apple,banana,orange,grape"))
//                .split(body().tokenize(","))
//                .log("Processing fruit: ${body}")
//                .end()
//                .log("All fruits processed");
//        from("direct:start")
//                .process(exchange -> {
//                    List<String> items = Arrays.asList("item1", "item2", "item3", "item4");
//                    exchange.getIn().setBody(items);
//                })
//                .split(body())
//                .log("Processing: ${body}")
//                .end();

        //json file
//        from("file:data/input?include=.*\\.json&noop=true")
//                .split(jsonpath("$.orders[*]"))
//                .log("Processing order: ${body}")
//                .end();
        //csv file
        from("file:data/input?include=.*\\.csv&noop=true")
                .log("=== Processing file: ${header.CamelFileName} ===")
                .split(body().tokenize("\n"))
                .choice()
                .when(simple("${header.CamelSplitIndex} == 0"))
                .log("Header row: ${body}")
                .otherwise()
                .log("Data row ${header.CamelSplitIndex}: ${body}")
                .end()
                .end()
                .log("=== File ${header.CamelFileName} processed ===");

    }


    @Override
    public void run(String... args) throws Exception {
        template.sendBody("direct:start", null);
    }
}
