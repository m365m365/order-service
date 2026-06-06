package com.example.orderservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gift_product")
public class Product {

    @Id
    private Long id;

    private String name;

    private Integer stock;

    public Product() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}