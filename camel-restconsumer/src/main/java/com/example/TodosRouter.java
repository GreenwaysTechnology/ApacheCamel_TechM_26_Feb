package com.example;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class TodosRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // Trigger route via timer, direct endpoint, or REST DSL
//        from("direct:getTodos")
//                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
//                .setHeader(Exchange.HTTP_QUERY, constant("param=value"))
//                .to("https://jsonplaceholder.typicode.com/todos")
//                //    .unmarshal().json(JsonLibrary.Jackson)  // Parse response
//                //               .log("Response: ${body}");
//                .convertBodyTo(String.class);
        from("direct:getTodos")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                // Use vertx-http instead of http
                .to("vertx-http:https://jsonplaceholder.typicode.com/todos?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .log("Vert.x Response: ${body}");

        //post request
        from("direct:createTodo")
                .routeId("create-todo-route")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("vertx-http:https://jsonplaceholder.typicode.com/todos")
                .log("Response : ${body}")
                .convertBodyTo(String.class);

        //put - update
        from("direct:updateTodo")
                .routeId("update-todo-route")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("todoId", simple("${body.id}"))
                .marshal().json()
                .setHeader(Exchange.HTTP_URI, simple("https://jsonplaceholder.typicode.com/todos/${header.todoId}"))
                .to("vertx-http:http://dummy")
                .log("Put Response :${body} ${header.todoId}")
                .convertBodyTo(String.class);

        //DELETE OPERAITON AND GET BY ID

    }
}
