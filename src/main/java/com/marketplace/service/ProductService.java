package com.marketplace.service;

import com.marketplace.model.Product;
import com.marketplace.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(String name, String description, BigDecimal price, int stockQuantity, String sellerId) {
        String id = UUID.randomUUID().toString();
        Product product = new Product(id, name, description, price, stockQuantity, sellerId);
        productRepository.save(product);
        return product;
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> listProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Product updateProduct(String id, String name, String description, BigDecimal price, int stockQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        if (name != null && !name.isBlank()) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) product.setPrice(price);
        if (stockQuantity >= 0) product.setStockQuantity(stockQuantity);
        productRepository.save(product);
        return product;
    }

    public boolean removeProduct(String id) {
        return productRepository.delete(id);
    }

    public boolean reduceStock(String productId, int quantity) {
        Optional<Product> opt = productRepository.findById(productId);
        if (opt.isEmpty()) return false;
        Product product = opt.get();
        if (product.getStockQuantity() < quantity) return false;
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        return true;
    }
}
