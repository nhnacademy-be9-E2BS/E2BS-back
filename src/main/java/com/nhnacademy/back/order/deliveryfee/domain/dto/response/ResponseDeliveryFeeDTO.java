package com.nhnacademy.back.order.deliveryfee.domain.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDeliveryFeeDTO {
	private long deliveryFeeId;
	private long deliveryFeeAmount;
	private long deliveryFeeFreeAmount;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deliveryFeeDate;

	public static ResponseDeliveryFeeDTO fromEntity(DeliveryFee deliveryFee) {
		return new ResponseDeliveryFeeDTO(
			deliveryFee.getDeliveryFeeId(),
			deliveryFee.getDeliveryFeeAmount(),
			deliveryFee.getDeliveryFeeFreeAmount(),
			deliveryFee.getDeliveryFeeDate());
	}
}
