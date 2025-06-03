package com.nhnacademy.back.order.order.domain.dto.response;

import java.time.LocalDateTime;

import com.nhnacademy.back.order.orderreturn.domain.entity.OrderReturn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderReturnDTO {
	private long id;
	private String orderCode;
	private String orderReturnReason;
	private String returnCategory;
	private LocalDateTime orderReturnCreatedAt;
	private long orderReturnAmount;

	public static ResponseOrderReturnDTO fromEntity(OrderReturn orderReturn) {
		return new ResponseOrderReturnDTO(
			orderReturn.getOrderReturnId(),
			orderReturn.getOrder().getOrderCode(),
			orderReturn.getOrderReturnReason(),
			orderReturn.getReturnCategory().name(),
			orderReturn.getOrderReturnCreatedAt(),
			orderReturn.getOrderReturnAmount()
		);
	}
}
