package com.nhnacademy.back.account.customer.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCustomerRegisterDTO {

	private String customerEmail;
	private String memberName;
	private String customerPassword;
	private String customerPasswordCheck;

}