package com.nhnacademy.back.order.paymentmethod.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethodName;

public interface PaymentMethodJpaRepository extends JpaRepository<PaymentMethod,Long> {
	Optional<PaymentMethod> findByPaymentMethodName(PaymentMethodName paymentMethodName);
}
