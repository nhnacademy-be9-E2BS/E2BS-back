package com.nhnacademy.back.cart.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게스트 장바구니 -> 회원 장바구니 병합 요청 DTO")
public class RequestMergeCartItemDTO {

	@NotBlank
	@Schema(description = "회원 ID (회원일 경우 필수)", example = "member123", requiredMode = Schema.RequiredMode.REQUIRED)
	private String memberId;

	@NotBlank
	@Schema(description = "게스트 세션 ID (게스트일 경우 필수)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
	private String sessionId;

}
