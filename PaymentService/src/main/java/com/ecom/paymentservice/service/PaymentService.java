package com.ecom.paymentservice.service;
import com.ecom.paymentservice.Exceptions.PaymentException;
import com.ecom.paymentservice.Gateway.PaymentGateway;
import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.repositories.PaymentRepostiory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentGateway paymentGateway;
    private final PaymentRepostiory paymentRepostiory;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    public PaymentService(PaymentGateway paymentGateway, PaymentRepostiory paymentRepostiory) {
        this.paymentGateway = paymentGateway;
        this.paymentRepostiory = paymentRepostiory;
    }

    public Payment initiatePayment(String userId,Long orderId,double amount,String method) throws PaymentException {
        //Make a call to Payment Gateway to generate the payment link.
        log.info("PaymentService initiatePayment");
        Payment payment= paymentGateway.processPayment(userId,orderId, amount,method);
        paymentRepostiory.save(payment);
        log.info("Payment successfully initiated");
        return payment;
    }
}
