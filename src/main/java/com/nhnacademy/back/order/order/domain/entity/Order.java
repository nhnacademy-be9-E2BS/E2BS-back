package com.nhnacademy.back.order.order.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.payment.domain.entity.Payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

	@Id
	private String orderCode;

	@Column(nullable = false, length = 20)
	private String orderReceiverName;

	@Column(nullable = false, length = 20)
	private String orderReceiverPhone;

	@Column(length = 20)
	private String orderReceiverTel;

	@Column(nullable = false, length = 5)
	private String orderAddressCode;

	@Column(nullable = false)
	private String orderAddressInfo;

	@Column
	private String orderAddressDetail;

	@Column(nullable = false)
	private String orderAddressExtra;

	@Column(columnDefinition = "BIGINT DEFAULT 0")
	private Long paymentPointAmount = 0L;

	@Column(columnDefinition = "TEXT")
	private String orderMemo;

	private LocalDateTime orderReceiveDate;

	private LocalDateTime orderShipmentDate;

	@Column(nullable = false)
	private LocalDateTime orderCreatedAt;

	@OneToOne
	@JoinColumn(name = "member_coupon_id")
	private MemberCoupon memberCoupon;

	@OneToOne(optional = false)
	@JoinColumn(name = "delivery_fee_id")
	private DeliveryFee deliveryFee;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToOne(optional = false)
	@JoinColumn(name = "payment_id")
	private Payment payment;

}
