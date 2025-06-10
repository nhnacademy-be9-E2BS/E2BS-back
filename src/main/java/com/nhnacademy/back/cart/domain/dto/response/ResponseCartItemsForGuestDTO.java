package com.nhnacademy.back.cart.domain.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게스트 장바구니 상품 응답 DTO")
public class ResponseCartItemsForGuestDTO {

	@Schema(description = "상품 ID", example = "1001")
	private long productId;

	@Schema(description = "상품 제목", example = "자바의 정석")
	private String productTitle;

	@Schema(description = "상품 판매가", example = "15000")
	private long productRegularPrice;

	@Schema(description = "상품 판매가", example = "15000")
	private long productSalePrice;

	@Schema(description = "상품 할인률", example = "10")
	private BigDecimal discountRate;

	@Schema(description = "상품 이미지 경로", example = "/images/products/java.jpg")
	private String productImagePath;

	@Schema(description = "장바구니에 담긴 수량", example = "2")
	private int cartItemsQuantity;

	@Schema(description = "상품 총 가격 (수량 × 단가)", example = "30000")
	private long productTotalPrice;

}
