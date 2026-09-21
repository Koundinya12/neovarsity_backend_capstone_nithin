package com.ecom.paymentservice.Gateway;

import com.ecom.paymentservice.Exceptions.PaymentException;
import com.ecom.paymentservice.PaymentGatewayClients.StripeClient;
import com.ecom.paymentservice.dto.PaymentRequestDto;
import com.ecom.paymentservice.dto.PaymentResponseDTO;

import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.models.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StripeGateway implements PaymentGateway {

    private StripeClient stripeClient;

    @Override
    public Payment processPayment(String userId,Long orderId, double amount,String method) throws PaymentException {
        String paymentLink="https://stripetest.com/"+userId+method;
        stripeClient.processPayment(userId,amount,paymentLink);
        Payment payment=new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setUserId(userId);
        payment.setId(Math.abs(new Random().nextLong()));
        payment.setStatus(PaymentStatus.SUCCESS.toString());
        payment.setMethod(method);
        payment.setGateway("Stripe");
        return payment;
    }

}
