package com.ecom.paymentservice.service;



import com.ecom.paymentservice.Exceptions.PaymentException;
import com.ecom.paymentservice.Gateway.PaymentGateway;
import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.repositories.PaymentRepostiory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentRepostiory paymentRepostiory;

    @InjectMocks
    private PaymentService paymentService;

    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setUserId("user123");
        mockPayment.setOrderId(1001L);
        mockPayment.setAmount(200.0);
        mockPayment.setMethod("UPI");
    }

    @Test
    void testInitiatePayment_Success() throws PaymentException {
        // Arrange
        when(paymentGateway.processPayment("user123", 1001L, 200.0, "UPI"))
                .thenReturn(mockPayment);
        when(paymentRepostiory.save(any(Payment.class)))
                .thenReturn(mockPayment);

        // Act
        Payment result = paymentService.initiatePayment("user123", 1001L, 200.0, "UPI");

        // Assert
        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertEquals(200.0, result.getAmount());
        verify(paymentGateway, times(1)).processPayment("user123", 1001L, 200.0, "UPI");
        verify(paymentRepostiory, times(1)).save(any(Payment.class));
    }

    @Test
    void testInitiatePayment_FailureFromGateway() throws PaymentException {
        // Arrange
        when(paymentGateway.processPayment(anyString(), anyLong(), anyDouble(), anyString()))
                .thenThrow(new PaymentException("Gateway error"));

        // Act & Assert
        PaymentException ex = assertThrows(PaymentException.class, () ->
                paymentService.initiatePayment("user123", 1001L, 200.0, "UPI")
        );

        assertEquals("Gateway error", ex.getMessage());
        verify(paymentRepostiory, never()).save(any());
    }
}
