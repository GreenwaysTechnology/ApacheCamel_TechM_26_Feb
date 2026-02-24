package com.example;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSpiltRouteStream extends RouteBuilder {

    @Value("${file.input.dir}")
    private String inputDir;

    @Value("${file.output.dir}")
    private String outputDir;

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Failed Processing: ${exception.message}")
                .handled(true)
                .to("file:data/errors");

        from("file:" + inputDir + "?noop=true&include=.*\\.csv")
                .routeId("csv-splitter")
                .log("Processing file: ${header.CamelFileName}")
                .split(body().tokenize("\n")).streaming()
                .filter(body().isNotNull())
                .filter(body().isNotEqualTo(""))
                .process(exchange -> {
                    String line = exchange.getIn().getBody(String.class);
                    exchange.getIn().setBody(line.trim());
                    exchange.getIn().setHeader("lineNumber",
                            exchange.getProperty(Exchange.SPLIT_INDEX));
                })
                .to("direct:processline")
                .end()
                .log("Finished File: ${header.CamelFileName}");

        from("direct:processline")
                .routeId("line-processor")
                .bean(LineProcessorService.class)
                .choice()
                .when(header("valid").isEqualTo(true))
                .to("file:" + outputDir + "?fileExist=Append&fileName=processed.csv")  // ✅ & not @
                .otherwise()
                .to("file:data/rejected?fileExist=Append&fileName=rejected.csv")
                .end();
    }
}
