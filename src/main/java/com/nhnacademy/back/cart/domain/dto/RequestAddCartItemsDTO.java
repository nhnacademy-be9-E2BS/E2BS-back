package com.nhnacademy.back.cart.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddCartItemsDTO {
	private long customerId;
	private long productId;
	private int quantity;
}
