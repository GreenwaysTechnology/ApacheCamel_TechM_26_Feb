package com.example;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarshallUnMarshall extends RouteBuilder implements CommandLineRunner {

    @Autowired
    private ProducerTemplate producerTemplate;
    @Override
    public void configure() throws Exception {
        // -----------------------------------------------
        // Route 1: Unmarshal single JSON object → Person
        // -----------------------------------------------
        from("direct:unmarshal-single")
                .log("Received JSON: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, Person.class)
                .log("Unmarshalled Person: ${body}")
                .process(exchange -> {
                    Person person = exchange.getIn().getBody(Person.class);
                    System.out.println("Processing person: " + person);
                    System.out.println("Name: " + person.getName());
                    System.out.println("Age:  " + person.getAge());
                })
                .to("direct:done");

        // -----------------------------------------------
        // Route 2: Unmarshal JSON array → List<Person>
        // -----------------------------------------------
        JacksonDataFormat listFormat = new JacksonDataFormat(Person.class);
        listFormat.useList();

        from("direct:unmarshal-list")
                .log("Received JSON array: ${body}")
                .unmarshal(listFormat)
                .log("Unmarshalled list: ${body}")
                .process(exchange -> {
                    @SuppressWarnings("unchecked")
                    List<Person> people = exchange.getIn().getBody(List.class);
                    System.out.println("Total people: " + people.size());
                    people.forEach(p -> System.out.println("  -> " + p));
                })
                .to("direct:done");

        // -----------------------------------------------
        // Route 3: Unmarshal JSON → generic Map (no POJO)
        // -----------------------------------------------
        from("direct:unmarshal-map")
                .log("Received JSON: ${body}")
                .unmarshal().json()   // defaults to Map<String, Object>
                .log("Unmarshalled Map: ${body}")
                .to("direct:done");

        // -----------------------------------------------
        // Route 4: Marshal Person → JSON (reverse)
        // -----------------------------------------------
        from("direct:marshal")
                .marshal().json(JsonLibrary.Jackson)
                .log("Marshalled JSON: ${body}")
                .to("direct:done");

        // Done sink
        from("direct:done")
                .log("Route complete.");


    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========== Test 1: Single Object ==========");
        String singleJson = """
                {
                    "name": "Alice",
                    "age": 30,
                    "email": "alice@example.com"
                }
                """;
        producerTemplate.sendBody("direct:unmarshal-single", singleJson);

        System.out.println("\n========== Test 2: JSON Array ==========");
        String arrayJson = """
                [
                    { "name": "Bob",   "age": 25, "email": "bob@example.com" },
                    { "name": "Carol", "age": 35, "email": "carol@example.com" },
                    { "name": "Dave",  "age": 40, "email": "dave@example.com" }
                ]
                """;
        producerTemplate.sendBody("direct:unmarshal-list", arrayJson);

        System.out.println("\n========== Test 3: Generic Map ==========");
        producerTemplate.sendBody("direct:unmarshal-map", singleJson);

        System.out.println("\n========== Test 4: Marshal POJO → JSON ==========");
        Person person = new Person("Eve", 28, "eve@example.com");
        producerTemplate.sendBody("direct:marshal", person);

    }
}
