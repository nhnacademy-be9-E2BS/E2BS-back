package com.nhnacademy.back.product.like.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "좋아요 생성 요청 DTO")
public class RequestCreateLikeDTO {

	@NotBlank
	@Schema(description = "회원 ID (로그인 회원용)", example = "member_abc123", requiredMode = Schema.RequiredMode.REQUIRED)
	private String memberId;

}
