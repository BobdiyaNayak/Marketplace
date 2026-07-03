package com.marketplace.model;

import java.math.BigDecimal;

public class OrderItem {

    private final String productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderItem(String productId, String productName, BigDecimal unitPrice, int quantity) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product id must not be blank");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Unit price must be non-negative");
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return String.format("OrderItem{product='%s', qty=%d, unitPrice=%s, subtotal=%s}",
                productName, quantity, unitPrice, getSubtotal());
    }
}
