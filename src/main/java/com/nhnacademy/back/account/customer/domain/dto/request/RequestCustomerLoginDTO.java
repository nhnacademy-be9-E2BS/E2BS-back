package com.nhnacademy.back.account.customer.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCustomerLoginDTO {

	private String customerEmail;
	private String customerPassword;

}
