package com.nhnacademy.back.cart.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게스트 장바구니 항목 삭제 요청 DTO")
public class RequestDeleteCartItemsForGuestDTO {

	@NotNull
	@Schema(description = "게스트 세션 ID (게스트일 경우 필수)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
	private String sessionId;

	@NotNull
	@Schema(description = "상품 ID", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;

}
