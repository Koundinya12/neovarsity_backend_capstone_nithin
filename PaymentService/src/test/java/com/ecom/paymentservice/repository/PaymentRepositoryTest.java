package com.ecom.paymentservice.repository;



import com.ecom.paymentservice.models.Payment;
import com.ecom.paymentservice.models.PaymentStatus;
import com.ecom.paymentservice.repositories.PaymentRepostiory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepostiory paymentRepostiory;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Payment payment = new Payment();
        payment.setUserId("user123");
        payment.setId(10L);
        payment.setOrderId(1001L);
        payment.setAmount(250.75);
        payment.setMethod("UPI");
        payment.setStatus(String.valueOf(PaymentStatus.SUCCESS));

        // Act
        Payment savedPayment = paymentRepostiory.save(payment);
        Optional<Payment> foundPayment = paymentRepostiory.findById(savedPayment.getId());

        // Assert
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getUserId()).isEqualTo("user123");
        assertThat(foundPayment.get().getAmount()).isEqualTo(250.75);
        assertThat(foundPayment.get().getStatus()).isEqualTo(String.valueOf(PaymentStatus.SUCCESS));
    }

    @Test
    void testDeletePayment() {
        // Arrange
        Payment payment = new Payment();
        payment.setUserId("user456");
        payment.setId(10L);
        payment.setOrderId(2002L);
        payment.setAmount(500.0);
        payment.setMethod("CARD");
        payment.setStatus(String.valueOf(PaymentStatus.PROCESSING));

        Payment savedPayment = paymentRepostiory.save(payment);

        // Act
        paymentRepostiory.deleteById(savedPayment.getId());

        // Assert
        Optional<Payment> deleted = paymentRepostiory.findById(savedPayment.getId());
        assertThat(deleted).isEmpty();
    }
}
