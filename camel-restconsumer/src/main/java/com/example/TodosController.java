package com.example;


import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Good practice to version your APIs
public class TodosController {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * This method calls the Camel route and returns the JSON.
     * Spring Boot automatically converts the Java List/Map returned by Camel
     * back into a JSON array/object because of the @RestController annotation.
     */
    @GetMapping(value = "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getAllTodos() {
        try {
            // We send 'null' as the body because GET requests don't have a request body
            // We request the result as 'Object.class' to let Camel return the Unmarshaled data
            return producerTemplate.requestBody("direct:getTodos", null, Object.class);
        } catch (Exception e) {
            // Simple error handling for production safety
            return "{\"error\": \"Failed to fetch data from third-party API: " + e.getMessage() + "\"}";
        }

    }

    @PostMapping(value = "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createTodo(@RequestBody TodoRequest request) {
        try {
            // We send 'null' as the body because GET requests don't have a request body
            // We request the result as 'Object.class' to let Camel return the Unmarshaled data
            return producerTemplate.requestBody("direct:createTodo", request, Object.class);
        } catch (Exception e) {
            // Simple error handling for production safety
            return "{\"error\": \"Failed to fetch data from third-party API: " + e.getMessage() + "\"}";
        }

    }

    @PutMapping(value = "/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateTodo(@PathVariable int id, @RequestBody TodoRequest request) {
        try {
            // We send 'null' as the body because GET requests don't have a request body
            // We request the result as 'Object.class' to let Camel return the Unmarshaled data
            request.setId(id);
            return producerTemplate.requestBody("direct:updateTodo", request, Object.class);
        } catch (Exception e) {
            // Simple error handling for production safety
            return "{\"error\": \"Failed to fetch data from third-party API: " + e.getMessage() + "\"}";
        }

    }
}
