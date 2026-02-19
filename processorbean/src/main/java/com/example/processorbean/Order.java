package com.example.processorbean;

public class Order {
    private String id;
    private String customerId;
    private double amount;
    private String status;
    private double discount;

    public Order() {

    }

    public Order(String id, String customerId, double amount, String status, double discount) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.discount = discount;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", discount=" + discount +
                '}';
    }
}
