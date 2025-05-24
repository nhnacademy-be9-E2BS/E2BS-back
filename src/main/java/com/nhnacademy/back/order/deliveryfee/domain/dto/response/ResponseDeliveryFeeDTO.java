package com.nhnacademy.back.order.deliveryfee.domain.dto.response;

import java.time.LocalDateTime;

import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDeliveryFeeDTO {
	private long deliveryFeeId;
	private long deliveryFeeAmount;
	private long deliveryFeeFreeAmount;
	private LocalDateTime deliveryFeeDate;

	public static ResponseDeliveryFeeDTO fromEntity(DeliveryFee deliveryFee) {
		return new ResponseDeliveryFeeDTO(
			deliveryFee.getDeliveryFeeId(),
			deliveryFee.getDeliveryFeeAmount(),
			deliveryFee.getDeliveryFeeFreeAmount(),
			deliveryFee.getDeliveryFeeDate());
	}
}
