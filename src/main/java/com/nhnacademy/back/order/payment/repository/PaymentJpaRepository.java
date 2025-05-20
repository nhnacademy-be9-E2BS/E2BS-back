package com.nhnacademy.back.order.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.payment.domain.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByOrderOrderCode(String orderCode);
}
