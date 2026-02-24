package com.example;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private String notificationId;
    private String orderId;
    private String customerId;
    private String message;
    private NotificationType type;

    @Builder.Default
    private String sentAt = java.time.LocalDateTime.now().toString(); // ←

    public enum NotificationType {
        ORDER_CONFIRMED, ORDER_FAILED, PAYMENT_PROCESSED, PAYMENT_FAILED
    }
}