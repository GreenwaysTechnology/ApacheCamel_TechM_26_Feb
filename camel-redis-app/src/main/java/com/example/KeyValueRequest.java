package com.example;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueRequest {
    private String key;
    private String value;
    private Long ttl; // optional TTL in seconds (Time to live) - timeout
}