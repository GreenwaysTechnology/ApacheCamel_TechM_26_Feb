package com.example;

public class Order {
    private String id;
    private String customerId;
    private double amount;
    private String product;

    // Constructors
    public Order() {}

    public Order(String id, String customerId, double amount, String product) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.product = product;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", amount=" + amount +
                ", product='" + product + '\'' +
                '}';
    }
}