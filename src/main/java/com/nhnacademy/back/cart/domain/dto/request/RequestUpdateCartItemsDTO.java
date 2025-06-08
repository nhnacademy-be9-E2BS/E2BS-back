package com.nhnacademy.back.cart.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 항목 수량 변경 요청 DTO")
public class RequestUpdateCartItemsDTO {

	@Schema(description = "회원 ID (회원일 경우 필수)", example = "member123", nullable = true)
	private String memberId;

	@Schema(description = "게스트 세션 ID (게스트일 경우 필수)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
	private String sessionId;

	@Schema(description = "상품 ID", example = "101", nullable = true)
	private Long productId;

	@NotNull
	@Schema(description = "담을 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer quantity;

}
