package com.nhnacademy.back.account.customer.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비회원 가입 요청 DTO")
public class RequestCustomerRegisterDTO {

	@Email
	@Schema(description = "비회원 이메일 주소", example = "guest@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	private String customerEmail;

	@NotBlank
	@Schema(description = "비회원 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
	private String customerName;

	@NotBlank
	@Schema(description = "비회원 비밀번호", example = "securePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
	private String customerPassword;

	@NotBlank
	@Schema(description = "비회원 비밀번호 확인", example = "securePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
	private String customerPasswordCheck;

}