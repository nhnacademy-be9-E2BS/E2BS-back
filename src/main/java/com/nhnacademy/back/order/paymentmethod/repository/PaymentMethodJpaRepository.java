package com.nhnacademy.back.order.paymentmethod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;

public interface PaymentMethodJpaRepository extends JpaRepository<PaymentMethod,Long> {
}
