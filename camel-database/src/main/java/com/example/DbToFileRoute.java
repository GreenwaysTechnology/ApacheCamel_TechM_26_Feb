package com.example;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbToFileRoute extends RouteBuilder {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void configure() {
        //DB → JSON File triggered by REST call ───────────────────
        // POST http://localhost:8080/api/export/trigger
        from("direct:triggerExport")
                .routeId("db-to-file-on-demand-route")
                .log(LoggingLevel.INFO, "⚡ On-demand export triggered!")

                .process(exchange -> {
                    List<User> users = userRepository.findAll();
                    exchange.getIn().setBody(users);
                    exchange.setProperty("recordCount", users.size());
                })

                .marshal().json(JsonLibrary.Jackson, true)

                .toD("file:data/output"
                        + "?fileName=users_ondemand_${date:now:yyyyMMdd_HHmmss}.json"
                        + "&fileExist=Override")
                .to("direct:db-to-csv")
                .log("✅ On-demand export done → ${header.CamelFileName}");


        from("direct:db-to-csv")
                .routeId("db-to-csv-route")
                .log("🔄 Starting DB → CSV export...")
                // Step 1: Fetch all users from DB
                .process(exchange -> {
                    List<User> users = userRepository.findAll();
                    exchange.setProperty("recordCount", users.size());

                    // Step 2: Build CSV string manually
                    StringBuilder csv = new StringBuilder();
                    csv.append("id,name,email,age\n"); // header row

                    for (User u : users) {
                        csv.append(u.getId()).append(",")
                                .append(escapeCsv(u.getName())).append(",")
                                .append(escapeCsv(u.getEmail())).append(",")
                                .append(u.getAge()).append("\n");
                    }

                    exchange.getIn().setBody(csv.toString());
                })

                // Step 3: Write CSV to file
                .toD("file:data/output"
                        + "?fileName=users_export_${date:now:yyyyMMdd_HHmmss}.csv"
                        + "&fileExist=Override")

                .log("✅ CSV export complete → ${header.CamelFileName} "
                        + "(${exchangeProperty.recordCount} records)");


    }

    // Wrap CSV field in quotes if it contains a comma
    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value + "\"" : value;
    }
}