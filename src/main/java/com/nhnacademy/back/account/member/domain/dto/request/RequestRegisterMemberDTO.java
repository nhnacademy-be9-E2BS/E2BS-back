package com.nhnacademy.back.account.member.domain.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegisterMemberDTO {

	@NotNull
	private String memberId;
	@NotNull
	private String customerName;
	@NotNull
	private String customerPassword;
	@NotNull
	private String customerPasswordCheck;
	@NotNull
	@Email
	private String customerEmail;
	@NotNull
	private LocalDate memberBirth;
	@NotNull
	private String memberPhone;

}
