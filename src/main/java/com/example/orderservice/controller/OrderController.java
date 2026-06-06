package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.Product;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5000")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {

        int total = 0;

        for (OrderItem item : order.getItems()) {

            Product product =
                    productRepository.findById(item.getProductId())
                            .orElseThrow();

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品庫存不足：" + item.getProductName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            item.setSubtotal(item.getPrice() * item.getQuantity());
            item.setOrder(order);
            total += item.getSubtotal();
        }

        order.setTotalAmount(total);
        order.setStatus("CREATED");
        order.setPaymentStatus("UNPAID");
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        String today =
                LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String orderNo =
                "TF" + today + String.format("%04d", savedOrder.getId());

        savedOrder.setOrderNo(orderNo);

        return orderRepository.save(savedOrder);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {

        Order order =
                orderRepository.findById(id).orElse(null);

        if (order == null) {
            return null;
        }

        if ("CANCELLED".equals(order.getStatus())) {
            return order;
        }

        for (OrderItem item : order.getItems()) {

            productRepository
                    .findById(item.getProductId())
                    .ifPresent(product -> {

                        product.setStock(
                                product.getStock()
                                        + item.getQuantity()
                        );

                        productRepository.save(product);
                    });
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

    @GetMapping("/member/{memberId}")
    public List<Order> getMemberOrders(@PathVariable Long memberId) {
        return orderRepository.findByMemberIdOrderByIdDesc(memberId);
    }
}