package com.nhnacademy.back.order.order.domain.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrderDTO {
	private Long memberCouponId;

	@NotNull
	private long deliveryFeeId;
	@NotNull
	private long customerId;

	@NotNull
	@Length(max = 20)
	private String orderReceiverName;
	@NotNull
	@Length(max = 20)
	private String orderReceiverPhone;
	@Length(max = 20)
	private String orderReceiverTel;

	@NotNull
	@Length(max = 5)
	private String orderAddressCode;
	@NotNull
	private String orderAddressInfo;
	private String orderAddressDetail;
	@NotNull
	private String orderAddressExtra;

	@NotNull
	private long orderPointAmount;
	private String orderMemo;
	@NotNull
	private boolean orderPaymentStatus;

	private LocalDate orderReceivedDate;
	private LocalDate orderShipmentDate;

	private LocalDateTime orderCreatedAt;

	@NotNull
	private long orderPaymentAmount;

}
