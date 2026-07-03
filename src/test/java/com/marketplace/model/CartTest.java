package com.marketplace.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void newCart_shouldBeEmpty() {
        Cart cart = new Cart("buyer1");
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void addItem_shouldAddProductToCart() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 2);

        assertFalse(cart.isEmpty());
        assertEquals(2, cart.getItems().get("prod1"));
    }

    @Test
    void addItem_shouldAccumulateQuantity_whenSameProductAddedTwice() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 3);
        cart.addItem("prod1", 2);

        assertEquals(5, cart.getItems().get("prod1"));
    }

    @Test
    void removeItem_shouldRemoveProductFromCart() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 1);
        cart.removeItem("prod1");

        assertTrue(cart.isEmpty());
    }

    @Test
    void updateQuantity_shouldSetNewQuantity() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 5);
        cart.updateQuantity("prod1", 2);

        assertEquals(2, cart.getItems().get("prod1"));
    }

    @Test
    void updateQuantity_shouldRemoveItem_whenQuantityIsZeroOrNegative() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 3);
        cart.updateQuantity("prod1", 0);

        assertFalse(cart.getItems().containsKey("prod1"));
    }

    @Test
    void clear_shouldEmptyCart() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 1);
        cart.addItem("prod2", 2);
        cart.clear();

        assertTrue(cart.isEmpty());
    }

    @Test
    void calculateTotal_shouldReturnCorrectTotal() {
        Cart cart = new Cart("buyer1");
        cart.addItem("prod1", 2);
        cart.addItem("prod2", 3);

        BigDecimal total = cart.calculateTotal(productId -> {
            if ("prod1".equals(productId)) return new BigDecimal("10.00");
            if ("prod2".equals(productId)) return new BigDecimal("5.00");
            return BigDecimal.ZERO;
        });

        assertEquals(new BigDecimal("35.00"), total);
    }

    @Test
    void addItem_shouldThrow_whenProductIdIsBlank() {
        Cart cart = new Cart("buyer1");
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("", 1));
    }

    @Test
    void addItem_shouldThrow_whenQuantityIsNotPositive() {
        Cart cart = new Cart("buyer1");
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("prod1", 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("prod1", -1));
    }
}
