package com.example;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonToJpaRoute extends RouteBuilder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileLogRepository fileLogRepository;

    @Override
    public void configure() {

        // ── Global error handler ──────────────────────────────────────────────
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,
                        "❌ Failed [${header.CamelFileName}]: ${exception.message}")
                .process(exchange -> {
                    String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
                    fileLogRepository.save(new CamelFileLog(fileName, 0, "ERROR"));
                });

        // ── Main Route ────────────────────────────────────────────────────────
        from("file:{{app.input.directory}}"
                + "?include=.*\\.json"
                + "&move={{app.input.done}}"
                + "&moveFailed={{app.input.error}}"
                + "&readLock=changed"
                + "&readLockCheckInterval=1000")
                .routeId("json-to-jpa-route")
                .log("📂 Picked up: ${header.CamelFileName}")

                // Step 1: Parse JSON array → User[]
                .unmarshal().json(JsonLibrary.Jackson, User[].class)
                .log("✅ Parsed ${body.length} records")

                // Step 2: Save all users via JPA repository
                .process(exchange -> {
                    User[] users = exchange.getIn().getBody(User[].class);

                    // Save all records in one batch
                    for (User user : users) {
                        userRepository.save(user);
                    }

                    // Store count for audit log step
                    exchange.setProperty("recordCount", users.length);

                    log.info("💾 Saved {} users to database", users.length);
                })

                // Step 3: Write audit log via JPA
                .process(exchange -> {
                    String fileName   = exchange.getIn().getHeader("CamelFileName", String.class);
                    int    count      = exchange.getProperty("recordCount", Integer.class);
                    fileLogRepository.save(new CamelFileLog(fileName, count, "SUCCESS"));
                })

                .log("🎉 Done: ${header.CamelFileName}");
    }
}