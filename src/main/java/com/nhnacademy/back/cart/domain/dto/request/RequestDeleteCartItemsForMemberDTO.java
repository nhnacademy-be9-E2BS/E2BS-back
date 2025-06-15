package com.nhnacademy.back.cart.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 장바구니 항목 삭제 요청 DTO")
public class RequestDeleteCartItemsForMemberDTO {

	@NotNull
	@Schema(description = "회원 ID", example = "memberId1", requiredMode = Schema.RequiredMode.REQUIRED)
	private String memberId;

	@NotNull
	@Schema(description = "상품 ID", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;

}
