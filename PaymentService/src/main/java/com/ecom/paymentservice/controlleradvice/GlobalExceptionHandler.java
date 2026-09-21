package com.ecom.paymentservice.controlleradvice;

import com.ecom.paymentservice.Exceptions.PaymentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> paymentExecptionHandler(PaymentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
