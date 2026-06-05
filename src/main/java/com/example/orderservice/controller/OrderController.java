package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5000")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {

        int total = 0;

        for (OrderItem item : order.getItems()) {
            item.setSubtotal(item.getPrice() * item.getQuantity());
            item.setOrder(order);
            total += item.getSubtotal();
        }

        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setTotalAmount(total);
        order.setStatus("CREATED");
        order.setPaymentStatus("UNPAID");
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {

        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) {
            return null;
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }
    @GetMapping("/member/{memberId}")
    public List<Order> getMemberOrders(
            @PathVariable Long memberId) {

        return orderRepository.findByMemberId(memberId);
    }

}
