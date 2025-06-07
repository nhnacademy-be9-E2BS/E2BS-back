package com.nhnacademy.back.cart.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 장바구니 상품 응답 DTO")
public class ResponseCartItemsForMemberDTO {

	@Schema(description = "장바구니 항목 ID", example = "1")
	private long cartItemsId;

	@Schema(description = "상품 ID", example = "1001")
	private long productId;

	@Schema(description = "상품 제목", example = "자바의 정석")
	private String productTitle;

	@Schema(description = "상품 판매가", example = "15000")
	private long productSalePrice;

	@Schema(description = "상품 이미지 경로", example = "/images/products/java.jpg")
	private String productImagePath;

	@Schema(description = "장바구니에 담긴 수량", example = "2")
	private int cartItemsQuantity;

	@Schema(description = "상품 총 가격 (수량 × 단가)", example = "30000")
	private long productTotalPrice;

}

