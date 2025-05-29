package com.nhnacademy.back.account.oauth.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOAuthRegisterDTO {

	private String memberId;
	private String email;
	private String mobile;
	private String name;
	private String birthdayMMdd;

}
