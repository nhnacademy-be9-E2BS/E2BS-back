package com.nhnacademy.back.account.oauth.model.dto.request;

import jakarta.validation.constraints.Email;
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
	@NotNull
	@Email
	private String email;
	@NotNull
	private String mobile;
	@NotNull
	private String name;
	@NotNull
	private String birthdayMMdd;

}
