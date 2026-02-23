package com.example;


import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ProducerTemplate producerTemplate;

    public ExportController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    // Trigger an on-demand export manually
    // POST http://localhost:8080/api/export/trigger
    @PostMapping("/trigger")
    public Map<String, String> triggerExport() {
        producerTemplate.sendBody("direct:triggerExport", null);
        return Map.of(
                "status",  "success",
                "message", "Export triggered. Check data/output/ for the file."
        );
    }
}