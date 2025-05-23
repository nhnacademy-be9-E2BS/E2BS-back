package com.nhnacademy.back.cart.domain.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCartOrderDTO {
	private List<Long> productIds;
	private List<Integer> cartQuantities;
}