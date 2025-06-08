package com.nhnacademy.back.cart.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 DTO")
public class CartDTO implements Serializable {
	
	@Schema(description = "장바구니 항목 리스트")
	private List<CartItemDTO> cartItems = new ArrayList<>();
	
}
