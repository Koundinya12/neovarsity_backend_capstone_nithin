package com.ecom.paymentservice.controller;



import com.ecom.common.events.PaymentEvent;
import com.ecom.paymentservice.Exceptions.PaymentException;
import com.ecom.paymentservice.KafkaConfig.PaymentProducer;
import com.ecom.paymentservice.dto.*;
import com.ecom.paymentservice.dto.PaymentRequestDto;
import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.models.PaymentStatus;
import com.ecom.paymentservice.service.PaymentService;
import com.ecom.paymentservice.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final AuthUtils authUtils;
    private final PaymentProducer paymentProducer;

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, AuthUtils authUtils, PaymentProducer paymentProducer) {
        this.paymentService = paymentService;
        this.authUtils = authUtils;
        this.paymentProducer = paymentProducer;
    }

    @PostMapping("/pay")
    public PaymentResponseDTO initiatePayment(@RequestBody PaymentRequestDto requestDto) throws PaymentException {
        Payment payment=paymentService.initiatePayment(
                    requestDto.getUserId(),
                    requestDto.getOrderId(),
                    requestDto.getAmount(),
                requestDto.getMethod());
            PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
            paymentResponseDTO.setPaymentId(payment.getId());
            paymentResponseDTO.setAmount(payment.getAmount());
            paymentResponseDTO.setUserId(requestDto.getUserId());
            paymentResponseDTO.setPaymentStatus(PaymentStatus.SUCCESS);
        PaymentEvent paymentEvent=new PaymentEvent();
        paymentEvent.setStatus("SUCCESS");
        paymentEvent.setAmount(requestDto.getAmount());
        paymentEvent.setOrderId(requestDto.getOrderId());
        paymentProducer.sendPaymentEvent(paymentEvent);
        log.info("Payment event sent");
        return paymentResponseDTO;
    }

}

