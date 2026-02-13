package com.example;

public class OrderRequest {
    private String orderId;
    private String priority;
    private Double amount;

    public OrderRequest(String orderId, String priority, Double amount) {
        this.orderId = orderId;
        this.priority = priority;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPriority() {
        return priority;
    }

    public Double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "OrderRequest{orderId='" + orderId + "', priority='" + priority +
                "', amount=" + amount + "}";
    }
}
