package com.example;

import jakarta.annotation.PostConstruct;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        from("direct:start")
                .setHeader("counter",constant(0))
                .loopDoWhile(simple("${header.counter} < 3"))
                .log("Counter => ${header.counter}")
                .process(e->{
                    int c = e.getIn().getHeader("counter",Integer.class);
                    e.getIn().setHeader("counter",c+1);
                })
                .end();
    }


    @Override
    public void run(String... args) throws Exception {
        template.sendBody("direct:start", null);
    }
}
