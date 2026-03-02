package com.example;


import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoRoutes extends RouteBuilder {

    private final MongoProcessor mongoProcessor;

    @Override
    public void configure() throws Exception {

        // Global Exception Handler
        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"error\":\"${exception.message}\"}"));

        // REST Configuration
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.off)
                .contextPath("/api");

        // ── REST Endpoints ──
        rest("/products")
                // Create
                .post()
                .consumes("application/json").produces("application/json")
                .to("direct:createProduct")
                // Get All
                .get()
                .produces("application/json")
                .to("direct:getAllProducts")
                // Get by ID
                .get("/{id}")
                .produces("application/json")
                .to("direct:getProductById")
                // Get by Category
                .get("/category/{category}")
                .produces("application/json")
                .to("direct:getByCategory")
                // Search by Name
                .get("/search/{name}")
                .produces("application/json")
                .to("direct:searchByName")
                // Update
                .put("/{id}")
                .consumes("application/json").produces("application/json")
                .to("direct:updateProduct")
                // Delete
                .delete("/{id}")
                .produces("application/json")
                .to("direct:deleteProduct");

        // ── Direct Routes ──

        from("direct:createProduct")
                .routeId("create-product")
                .bean(mongoProcessor, "create");

        from("direct:getAllProducts")
                .routeId("get-all-products")
                .bean(mongoProcessor, "getAll");

        from("direct:getProductById")
                .routeId("get-product-by-id")
                .bean(mongoProcessor, "getById");

        from("direct:getByCategory")
                .routeId("get-by-category")
                .bean(mongoProcessor, "getByCategory");

        from("direct:searchByName")
                .routeId("search-by-name")
                .bean(mongoProcessor, "searchByName");

        from("direct:updateProduct")
                .routeId("update-product")
                .bean(mongoProcessor, "update");

        from("direct:deleteProduct")
                .routeId("delete-product")
                .bean(mongoProcessor, "delete");
    }
}