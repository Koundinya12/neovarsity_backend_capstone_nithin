package com.ecom.paymentservice.Gateway;

import com.ecom.paymentservice.Exceptions.PaymentException;
import com.ecom.paymentservice.PaymentGatewayClients.RazorPayClient;
import com.ecom.paymentservice.dto.PaymentRequestDto;
import com.ecom.paymentservice.dto.PaymentResponseDTO;
import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.models.PaymentStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Primary
public class RazorPayGateway implements PaymentGateway {

    private RazorPayClient razorpayClient;

    public RazorPayGateway(RazorPayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
    public Payment processPayment(String userId,Long orderId, double amount,String method) throws PaymentException {
        String paymentLink="https://razorpaytest.com/"+userId+method;
        razorpayClient.doPayment(userId,amount,paymentLink);
        Payment payment=new Payment();
        payment.setAmount(amount);
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setId(Math.abs(new Random().nextLong()));
        payment.setStatus(PaymentStatus.SUCCESS.toString());
        payment.setMethod(method);
        payment.setGateway("RazorPay");
        return payment;
    }

}
