package com.nhnacademy.back.account.member.domain.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMemberInfoDTO {

	@NotNull
	private String memberId;
	@NotNull
	private String customerName;
	@NotNull
	@Email
	private String customerEmail;
	@NotNull
	private LocalDate memberBirth;
	@NotNull
	private String memberPhone;

}
