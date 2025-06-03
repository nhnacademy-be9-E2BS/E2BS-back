package com.nhnacademy.back.account.customer.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCustomerLoginDTO {

	@Email
	private String customerEmail;
	@NotBlank
	private String customerPassword;

}
