package com.nhnacademy.back.account.member.domain.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRegisterMemberDTO {

	private String memberId;
	private String customerName;
	private String customerPassword;
	private String customerEmail;
	private LocalDate memberBirth;
	private String memberPhone;

}
