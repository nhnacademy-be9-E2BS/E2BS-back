package com.nhnacademy.back.account.member.domain.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
