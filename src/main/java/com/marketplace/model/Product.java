package com.marketplace.model;

import java.math.BigDecimal;

public class Product {

    private final String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String sellerId;

    public Product(String id, String name, String description, BigDecimal price, int stockQuantity, String sellerId) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Product id must not be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name must not be blank");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price must be non-negative");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock quantity must be non-negative");
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.sellerId = sellerId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getSellerId() { return sellerId; }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', price=%s, stock=%d}", id, name, price, stockQuantity);
    }
}
