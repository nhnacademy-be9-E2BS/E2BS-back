package com.nhnacademy.back.account.customer.domain.dto.response;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerRegisterDTO {

	private Customer customer;

}