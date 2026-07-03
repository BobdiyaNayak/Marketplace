package com.marketplace.service;

import com.marketplace.model.Cart;
import com.marketplace.model.Order;
import com.marketplace.model.OrderItem;
import com.marketplace.model.Product;
import com.marketplace.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public Order placeOrder(Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (var entry : cart.getItems().entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productService.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            if (product.getStockQuantity() < quantity) {
                throw new IllegalStateException(
                        "Insufficient stock for product '" + product.getName() + "'. Available: " + product.getStockQuantity());
            }

            orderItems.add(new OrderItem(product.getId(), product.getName(), product.getPrice(), quantity));
        }

        // Deduct stock after validating all items
        for (OrderItem item : orderItems) {
            productService.reduceStock(item.getProductId(), item.getQuantity());
        }

        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, cart.getBuyerId(), orderItems);
        orderRepository.save(order);
        cart.clear();
        return order;
    }

    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> listOrdersByBuyer(String buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(String orderId, Order.Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        return order;
    }
}
