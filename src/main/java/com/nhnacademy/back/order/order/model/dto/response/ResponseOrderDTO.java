package com.nhnacademy.back.order.order.model.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nhnacademy.back.order.order.model.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDTO {
	private long customerId;
	private String memberId;
	private boolean isMember;
	private long deliveryFeeId;
	private long deliveryFeeAmount;
	private long deliveryFeeFreeAmount;
	private Long memberCouponId;
	private String orderCode;
	private String receiverName;
	private String receiverPhone;
	private String receiverTel;
	private String addressCode;
	private String addressInfo;
	private String addressDetail;
	private String addressExtra;
	private long pointAmount;
	private long paymentAmount;
	private String memo;
	private boolean isPaid;
	private LocalDate receiveDate;
	private LocalDate shipmentDate;
	private LocalDateTime createdAt;
	private String state;
	private String paymentMethod;

	// 엔티티를 DTO로 바꾸는 메서드
	public static ResponseOrderDTO fromEntity(Order order) {
		return new ResponseOrderDTO(
			order.getCustomer().getCustomerId(),
			null,
			false,
			order.getDeliveryFee().getDeliveryFeeId(),
			order.getDeliveryFee().getDeliveryFeeAmount(),
			order.getDeliveryFee().getDeliveryFeeFreeAmount(),
			order.getMemberCoupon() != null ? order.getMemberCoupon().getMemberCouponId() : null,
			order.getOrderCode(),
			order.getOrderReceiverName(),
			order.getOrderReceiverPhone(),
			order.getOrderReceiverTel(),
			order.getOrderAddressCode(),
			order.getOrderAddressInfo(),
			order.getOrderAddressDetail(),
			order.getOrderAddressExtra(),
			order.getOrderPointAmount(),
			order.getOrderPaymentAmount(),
			order.getOrderMemo(),
			order.isOrderPaymentStatus(),
			order.getOrderReceiveDate(),
			order.getOrderShipmentDate(),
			order.getOrderCreatedAt(),
			order.getOrderState().getOrderStateName().name(),
			null
		);
	}
}
