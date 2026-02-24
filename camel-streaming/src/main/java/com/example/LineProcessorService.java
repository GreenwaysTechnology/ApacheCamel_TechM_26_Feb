package com.example;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LineProcessorService {

    private static final Logger log = LoggerFactory.getLogger(LineProcessorService.class);

    public void process(Exchange exchange) {
        String line = exchange.getIn().getBody(String.class);
        int lineNum = exchange.getIn().getHeader("lineNumber", Integer.class);

        // Skip header row
        if (lineNum == 0) {
            exchange.getIn().setHeader("valid", false);
            return;
        }

        String[] fields = line.split(",");
        if (fields.length < 3) {
            log.warn("Invalid line {}: {}", lineNum, line);
            exchange.getIn().setHeader("valid", false);
            return;
        }

        // Transform / enrich
        String processed = String.join(",", fields).toUpperCase();
        exchange.getIn().setBody(processed + "\n");
        exchange.getIn().setHeader("valid", true);

        log.info("Processed line {}: {}", lineNum, processed);
    }
}