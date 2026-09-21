package com.ecom.paymentservice.repositories;

import com.ecom.paymentservice.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepostiory extends JpaRepository<Payment, Long> {

}
