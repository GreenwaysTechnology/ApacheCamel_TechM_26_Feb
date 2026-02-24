package com.example;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Builder.Default
    private String orderId = UUID.randomUUID().toString();

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Builder.Default
    private String createdAt = java.time.LocalDateTime.now().toString();

    private String errorMessage;

    public enum OrderStatus {
        PENDING, VALIDATED, PROCESSING, COMPLETED, FAILED
    }
}