package com.nhnacademy.back.order.order.domain.entity;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;

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

	@Column(nullable = false)
	private long orderPointAmount = 0L;

	@Column(nullable = false)
	private long orderPaymentAmount;

	@Column(columnDefinition = "TEXT")
	private String orderMemo;

	@Column(nullable = false)
	private boolean orderPaymentStatus = false;

	// 희망 수령일, 출고일은 Date 타입으로 변경
	private LocalDate orderReceiveDate;

	private LocalDate orderShipmentDate;

	@Column(nullable = false)
	private LocalDateTime orderCreatedAt;

	@OneToOne
	@JoinColumn(name = "member_coupon_id")
	private MemberCoupon memberCoupon;

	// 일대일일 경우 한 정책을 한번밖에 사용 못 함 -> 다대일로 수정
	@ManyToOne(optional = false)
	@JoinColumn(name = "delivery_fee_id")
	private DeliveryFee deliveryFee;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_state_id")
	private OrderState orderState;

	public void updatePaymentStatus(boolean status) {
		this.orderPaymentStatus = status;
	}

	public void updateOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	public void updateOrderShipmentDate(LocalDate orderShipmentDate) {
		this.orderShipmentDate = orderShipmentDate;
	}

	public Order(RequestOrderDTO requestOrderDTO, MemberCoupon memberCoupon, DeliveryFee deliveryFee,
		Customer customer, OrderState orderState) {
		this.orderCode = generateSecureOrderId();
		this.orderReceiverName = requestOrderDTO.getOrderReceiverName();
		this.orderReceiverPhone = requestOrderDTO.getOrderReceiverPhone();
		this.orderReceiverTel = requestOrderDTO.getOrderReceiverTel();
		this.orderAddressCode = requestOrderDTO.getOrderAddressCode();
		this.orderAddressInfo = requestOrderDTO.getOrderAddressInfo();
		this.orderAddressDetail = requestOrderDTO.getOrderAddressDetail();
		this.orderAddressExtra = requestOrderDTO.getOrderAddressExtra();
		this.orderPointAmount = requestOrderDTO.getOrderPointAmount();
		this.orderMemo = requestOrderDTO.getOrderMemo();
		this.orderPaymentStatus = false;
		this.orderReceiveDate = requestOrderDTO.getOrderReceivedDate();
		this.orderShipmentDate = requestOrderDTO.getOrderShipmentDate();
		this.orderCreatedAt = LocalDateTime.now();
		this.orderPaymentAmount = requestOrderDTO.getOrderPaymentAmount();
		this.memberCoupon = memberCoupon;
		this.customer = customer;
		this.deliveryFee = deliveryFee;
		this.orderState = orderState;
	}

	/**
	 * 주문 ID 난수를 생성하는 메서드
	 * 임의로 32글자로 생성
	 */
	private String generateSecureOrderId() {
		// 영문 대소문자, 숫자, -, _ 포함된 32자리 문자열 생성 로직
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
		StringBuilder sb = new StringBuilder();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < 32; i++) {
			int idx = random.nextInt(base.length());
			sb.append(base.charAt(idx));
		}
		return sb.toString();
	}
}
