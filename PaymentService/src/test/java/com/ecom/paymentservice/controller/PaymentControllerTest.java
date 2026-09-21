package com.ecom.paymentservice.controller;


import com.ecom.common.events.PaymentEvent;
import com.ecom.paymentservice.KafkaConfig.PaymentProducer;
import com.ecom.paymentservice.controller.PaymentController;
import com.ecom.paymentservice.dto.PaymentRequestDto;
import com.ecom.paymentservice.dto.PaymentResponseDTO;
import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.models.PaymentStatus;
import com.ecom.paymentservice.security.JwtService;
import com.ecom.paymentservice.service.PaymentService;
import com.ecom.paymentservice.utils.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private PaymentProducer paymentProducer;

    @MockitoBean
    private JwtService  jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequestDto requestDto;
    private Payment mockPayment;

    @BeforeEach
    void setup() {
        requestDto = new PaymentRequestDto();
        requestDto.setUserId("user123");
        requestDto.setOrderId(123L);
        requestDto.setAmount(250.0);
        requestDto.setMethod("CARD");

        mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setUserId("user123");
        mockPayment.setAmount(250.0);
        mockPayment.setStatus(String.valueOf(PaymentStatus.SUCCESS));
    }

    @Test
    void testInitiatePayment_Success() throws Exception {
        // Arrange
        when(paymentService.initiatePayment(
                anyString(), anyLong(), anyDouble(), anyString())
        ).thenReturn(mockPayment);

        // Act & Assert
        mockMvc.perform(post("/payments/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(mockPayment.getId()))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.amount").value(250.0))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));

        // Verify Kafka event was sent
        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);
        verify(paymentProducer, times(1)).sendPaymentEvent(eventCaptor.capture());

        PaymentEvent sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getStatus()).isEqualTo("SUCCESS");
        assertThat(sentEvent.getOrderId()).isEqualTo(123L);
        assertThat(sentEvent.getAmount()).isEqualTo(250.0);
    }
}
