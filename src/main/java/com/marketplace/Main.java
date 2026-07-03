package com.marketplace;

import com.marketplace.model.Cart;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.OrderService;
import com.marketplace.service.ProductService;
import com.marketplace.service.UserService;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Bootstrap repositories
        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();

        // Bootstrap services
        ProductService productService = new ProductService(productRepository);
        UserService userService = new UserService(userRepository);
        OrderService orderService = new OrderService(orderRepository, productService);

        System.out.println("=== Marketplace Sample Application ===\n");

        // Register users
        User seller = userService.registerUser("Alice Smith", "alice@example.com", User.Role.SELLER);
        User buyer = userService.registerUser("Bob Jones", "bob@example.com", User.Role.BUYER);
        System.out.println("Registered users:");
        userService.listAllUsers().forEach(u -> System.out.println("  " + u));

        System.out.println();

        // Seller lists products
        Product laptop = productService.addProduct(
                "Laptop Pro", "High-performance laptop", new BigDecimal("1299.99"), 10, seller.getId());
        Product headphones = productService.addProduct(
                "Wireless Headphones", "Noise-cancelling headphones", new BigDecimal("199.99"), 50, seller.getId());
        Product mouse = productService.addProduct(
                "Ergonomic Mouse", "Comfortable ergonomic mouse", new BigDecimal("49.99"), 100, seller.getId());

        System.out.println("Available products:");
        productService.listAllProducts().forEach(p -> System.out.println("  " + p));

        System.out.println();

        // Buyer adds items to cart and places an order
        Cart cart = new Cart(buyer.getId());
        cart.addItem(laptop.getId(), 1);
        cart.addItem(headphones.getId(), 2);
        System.out.println("Cart: " + cart);

        Order order = orderService.placeOrder(cart);
        System.out.println("\nOrder placed successfully:");
        System.out.println("  " + order);
        System.out.println("  Items:");
        order.getItems().forEach(item -> System.out.println("    " + item));

        System.out.println();

        // Update order status
        orderService.updateOrderStatus(order.getId(), Order.Status.CONFIRMED);
        orderService.updateOrderStatus(order.getId(), Order.Status.SHIPPED);
        System.out.println("Order status updated: " + orderService.findById(order.getId()).get().getStatus());

        System.out.println();

        // Show remaining stock
        System.out.println("Updated product stock:");
        productService.listAllProducts().forEach(p -> System.out.println("  " + p));

        System.out.println();

        // Show buyer's order history
        List<Order> buyerOrders = orderService.listOrdersByBuyer(buyer.getId());
        System.out.println("Buyer '" + buyer.getName() + "' orders: " + buyerOrders.size());
        buyerOrders.forEach(o -> System.out.println("  Total: $" + o.getTotal() + ", Status: " + o.getStatus()));

        System.out.println("\n=== Done ===");
    }
}
