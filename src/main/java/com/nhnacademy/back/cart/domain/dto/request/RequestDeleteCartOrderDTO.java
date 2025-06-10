package com.nhnacademy.back.cart.domain.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문한 상품 항목을 장바구니 항목에 삭제 요청 DTO")
public class RequestDeleteCartOrderDTO {

	@Schema(description = "회원 아이디", example = "member123")
	private String memberId;

	@Schema(description = "게스트 세션 ID (게스트일 경우 필수)", example = "550e8400-e29b-41d4-a716-446655440000")
	private String sessionId;

	@NotNull
	@Schema(description = "상품 ID 리스트", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<Long> productIds;

	@NotNull
	@Schema(description = "상품별 수량 리스트 (productIds의 순서에 대응)", example = "[2, 1, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<Integer> cartQuantities;

}
