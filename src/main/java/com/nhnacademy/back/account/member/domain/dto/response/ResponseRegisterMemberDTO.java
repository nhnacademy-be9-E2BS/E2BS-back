package com.nhnacademy.back.account.member.domain.dto.response;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseRegisterMemberDTO {

	@NotNull
	private String memberId;
	@NotNull
	private String customerName;
	@NotNull
	private String customerPassword;
	@NotNull
	private String customerEmail;
	@NotNull
	private LocalDate memberBirth;
	@NotNull
	private String memberPhone;

}
