package com.marketplace.service;

import com.marketplace.model.Cart;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private ProductService productService;
    private OrderService orderService;
    private String sellerId;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();

        productService = new ProductService(productRepository);
        UserService userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, productService);

        User seller = userService.registerUser("Seller", "seller@example.com", User.Role.SELLER);
        sellerId = seller.getId();
    }

    private Product createProduct(String name, BigDecimal price, int stock) {
        return productService.addProduct(name, "desc", price, stock, sellerId);
    }

    @Test
    void placeOrder_shouldCreateOrderAndReduceStock() {
        Product product = createProduct("Widget", new BigDecimal("10.00"), 5);
        Cart cart = new Cart("buyer1");
        cart.addItem(product.getId(), 2);

        Order order = orderService.placeOrder(cart);

        assertNotNull(order.getId());
        assertEquals("buyer1", order.getBuyerId());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("20.00"), order.getTotal());
        assertEquals(Order.Status.PENDING, order.getStatus());

        int remainingStock = productService.findById(product.getId()).get().getStockQuantity();
        assertEquals(3, remainingStock);
    }

    @Test
    void placeOrder_shouldClearCartAfterOrder() {
        Product product = createProduct("Gadget", new BigDecimal("5.00"), 10);
        Cart cart = new Cart("buyer1");
        cart.addItem(product.getId(), 1);

        orderService.placeOrder(cart);

        assertTrue(cart.isEmpty());
    }

    @Test
    void placeOrder_shouldThrow_whenCartIsEmpty() {
        Cart cart = new Cart("buyer1");

        assertThrows(IllegalStateException.class, () -> orderService.placeOrder(cart));
    }

    @Test
    void placeOrder_shouldThrow_whenInsufficientStock() {
        Product product = createProduct("Rare Item", new BigDecimal("100.00"), 1);
        Cart cart = new Cart("buyer1");
        cart.addItem(product.getId(), 5);

        assertThrows(IllegalStateException.class, () -> orderService.placeOrder(cart));
    }

    @Test
    void placeOrder_shouldThrow_whenProductNotFound() {
        Cart cart = new Cart("buyer1");
        cart.addItem("nonexistent-product", 1);

        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(cart));
    }

    @Test
    void findById_shouldReturnOrder_whenExists() {
        Product product = createProduct("Item", new BigDecimal("1.00"), 10);
        Cart cart = new Cart("buyer1");
        cart.addItem(product.getId(), 1);
        Order order = orderService.placeOrder(cart);

        Optional<Order> found = orderService.findById(order.getId());

        assertTrue(found.isPresent());
        assertEquals(order.getId(), found.get().getId());
    }

    @Test
    void listOrdersByBuyer_shouldFilterByBuyerId() {
        Product p1 = createProduct("P1", new BigDecimal("10.00"), 5);
        Product p2 = createProduct("P2", new BigDecimal("20.00"), 5);

        Cart cartA = new Cart("buyerA");
        cartA.addItem(p1.getId(), 1);
        orderService.placeOrder(cartA);

        Cart cartB = new Cart("buyerB");
        cartB.addItem(p2.getId(), 1);
        orderService.placeOrder(cartB);

        Cart cartA2 = new Cart("buyerA");
        cartA2.addItem(p1.getId(), 1);
        orderService.placeOrder(cartA2);

        List<Order> buyerAOrders = orderService.listOrdersByBuyer("buyerA");
        assertEquals(2, buyerAOrders.size());
        assertTrue(buyerAOrders.stream().allMatch(o -> "buyerA".equals(o.getBuyerId())));
    }

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        Product product = createProduct("Product", new BigDecimal("50.00"), 10);
        Cart cart = new Cart("buyer1");
        cart.addItem(product.getId(), 1);
        Order order = orderService.placeOrder(cart);

        Order updated = orderService.updateOrderStatus(order.getId(), Order.Status.CONFIRMED);

        assertEquals(Order.Status.CONFIRMED, updated.getStatus());
    }

    @Test
    void updateOrderStatus_shouldThrow_whenOrderNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("bad-id", Order.Status.SHIPPED));
    }

    @Test
    void placeOrder_multipleItems_shouldCalculateTotalCorrectly() {
        Product p1 = createProduct("A", new BigDecimal("10.00"), 10);
        Product p2 = createProduct("B", new BigDecimal("25.00"), 10);
        Cart cart = new Cart("buyer1");
        cart.addItem(p1.getId(), 3);  // 30.00
        cart.addItem(p2.getId(), 2);  // 50.00

        Order order = orderService.placeOrder(cart);

        assertEquals(new BigDecimal("80.00"), order.getTotal());
    }
}
