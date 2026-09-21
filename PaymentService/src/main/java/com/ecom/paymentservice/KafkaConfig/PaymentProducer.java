package com.ecom.paymentservice.KafkaConfig;

import com.ecom.common.events.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPaymentEvent(PaymentEvent event) {
        kafkaTemplate.send("payments", event);
        System.out.println("Produced Payment Event: " + event);
    }
}