package com.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MongoProcessor {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    // ── CREATE ──
    public void create(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        Product product = objectMapper.readValue(body, Product.class);
        Product saved = mongoTemplate.save(product);
        exchange.getIn().setBody(objectMapper.writeValueAsString(saved));
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    // ── GET ALL ──
    public void getAll(Exchange exchange) throws Exception {
        List<Product> products = mongoTemplate.findAll(Product.class);
        exchange.getIn().setBody(objectMapper.writeValueAsString(products));
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    // ── GET BY ID ──
    public void getById(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        Product product = mongoTemplate.findById(id, Product.class);
        if (product == null) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody("{\"error\":\"Product not found\"}");
        } else {
            exchange.getIn().setBody(objectMapper.writeValueAsString(product));
        }
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    // ── GET BY CATEGORY ──
    public void getByCategory(Exchange exchange) throws Exception {
        String category = exchange.getIn().getHeader("category", String.class);
        Query query = new Query(Criteria.where("category").is(category));
        List<Product> products = mongoTemplate.find(query, Product.class);
        exchange.getIn().setBody(objectMapper.writeValueAsString(products));
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    // ── UPDATE ──
    public void update(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        String body = exchange.getIn().getBody(String.class);
        Product updated = objectMapper.readValue(body, Product.class);

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update()
                .set("name", updated.getName())
                .set("category", updated.getCategory())
                .set("price", updated.getPrice())
                .set("quantity", updated.getQuantity());

        mongoTemplate.updateFirst(query, update, Product.class);
        Product result = mongoTemplate.findById(id, Product.class);
        exchange.getIn().setBody(objectMapper.writeValueAsString(result));
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    // ── DELETE ──
    public void delete(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        mongoTemplate.remove(query, Product.class);
        exchange.getIn().setBody("{\"status\":\"deleted\",\"id\":\"" + id + "\"}");
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    // ── SEARCH BY NAME ──
    public void searchByName(Exchange exchange) throws Exception {
        String name = exchange.getIn().getHeader("name", String.class);
        Query query = new Query(Criteria.where("name").regex(name, "i")); // case-insensitive
        List<Product> products = mongoTemplate.find(query, Product.class);
        exchange.getIn().setBody(objectMapper.writeValueAsString(products));
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }
}