package com.nhnacademy.back.account.oauth.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOAuthRegisterDTO {

	@NotNull
	private String memberId;
	private String email;
	private String mobile;
	private String name;
	private String birthdayMMdd;

}
