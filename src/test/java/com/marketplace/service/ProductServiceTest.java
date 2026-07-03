package com.marketplace.service;

import com.marketplace.model.Product;
import com.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(new ProductRepository());
    }

    @Test
    void addProduct_shouldPersistAndReturnProduct() {
        Product product = productService.addProduct("Laptop", "A great laptop", new BigDecimal("999.99"), 5, "seller1");

        assertNotNull(product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(new BigDecimal("999.99"), product.getPrice());
        assertEquals(5, product.getStockQuantity());
        assertEquals("seller1", product.getSellerId());
    }

    @Test
    void findById_shouldReturnProduct_whenExists() {
        Product product = productService.addProduct("Phone", "Smartphone", new BigDecimal("599.00"), 20, "seller1");

        Optional<Product> found = productService.findById(product.getId());

        assertTrue(found.isPresent());
        assertEquals("Phone", found.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Product> found = productService.findById("nonexistent");

        assertTrue(found.isEmpty());
    }

    @Test
    void listAllProducts_shouldReturnAllAddedProducts() {
        productService.addProduct("A", "desc", new BigDecimal("10.00"), 1, "s1");
        productService.addProduct("B", "desc", new BigDecimal("20.00"), 2, "s2");

        List<Product> products = productService.listAllProducts();

        assertEquals(2, products.size());
    }

    @Test
    void listProductsBySeller_shouldFilterBySellerId() {
        productService.addProduct("A", "desc", new BigDecimal("10.00"), 1, "sellerA");
        productService.addProduct("B", "desc", new BigDecimal("20.00"), 2, "sellerB");
        productService.addProduct("C", "desc", new BigDecimal("30.00"), 3, "sellerA");

        List<Product> sellerAProducts = productService.listProductsBySeller("sellerA");

        assertEquals(2, sellerAProducts.size());
        assertTrue(sellerAProducts.stream().allMatch(p -> "sellerA".equals(p.getSellerId())));
    }

    @Test
    void updateProduct_shouldModifyFields() {
        Product product = productService.addProduct("Old Name", "Old desc", new BigDecimal("10.00"), 5, "s1");

        Product updated = productService.updateProduct(product.getId(), "New Name", "New desc", new BigDecimal("15.00"), 10);

        assertEquals("New Name", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(new BigDecimal("15.00"), updated.getPrice());
        assertEquals(10, updated.getStockQuantity());
    }

    @Test
    void updateProduct_shouldThrow_whenProductNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.updateProduct("bad-id", "Name", "Desc", new BigDecimal("1.00"), 1));
    }

    @Test
    void removeProduct_shouldReturnTrue_whenExists() {
        Product product = productService.addProduct("X", "desc", new BigDecimal("5.00"), 1, "s1");

        boolean removed = productService.removeProduct(product.getId());

        assertTrue(removed);
        assertTrue(productService.findById(product.getId()).isEmpty());
    }

    @Test
    void removeProduct_shouldReturnFalse_whenNotExists() {
        boolean removed = productService.removeProduct("nonexistent");

        assertFalse(removed);
    }

    @Test
    void reduceStock_shouldDecreaseQuantity_whenSufficientStock() {
        Product product = productService.addProduct("Stock Item", "desc", new BigDecimal("5.00"), 10, "s1");

        boolean result = productService.reduceStock(product.getId(), 3);

        assertTrue(result);
        assertEquals(7, productService.findById(product.getId()).get().getStockQuantity());
    }

    @Test
    void reduceStock_shouldReturnFalse_whenInsufficientStock() {
        Product product = productService.addProduct("Low Stock", "desc", new BigDecimal("5.00"), 2, "s1");

        boolean result = productService.reduceStock(product.getId(), 5);

        assertFalse(result);
        assertEquals(2, productService.findById(product.getId()).get().getStockQuantity());
    }
}
