package com.nhnacademy.back.account.customer.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerDTO {
	private String customerName;

	private long customerId;
}
