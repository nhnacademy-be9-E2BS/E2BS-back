package com.nhnacademy.back.order.payment.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long paymentId;

	@Column(nullable = false)
	private String paymentKey;

	@Column(nullable = false)
	private long totalPaymentAmount;

	@Column(nullable = false)
	private LocalDateTime paymentRequestedAt;

	private LocalDateTime paymentApprovedAt;

	@OneToOne(optional = false)
	@JoinColumn(name = "payment_method_id")
	private PaymentMethod paymentMethod;

	@OneToOne(optional = false)
	@JoinColumn(name = "order_code")
	private Order order;
}
