package com.ecom.orderservice.KafkaConfig;

import com.ecom.common.events.PaymentEvent;
import com.ecom.orderservice.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payments", groupId = "payments-group")
    public void consumePayment(PaymentEvent event) {

        System.out.println("Consumed Payment Event: " + event);
        orderService.updateOrderStatus(event);
    }
}
