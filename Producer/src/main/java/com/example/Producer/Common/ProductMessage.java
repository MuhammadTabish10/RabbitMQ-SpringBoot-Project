package com.example.Producer.Common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMessage {
    private String productId;
    private String productName;
    private Double price;
    private String description;
}