package com.nhnacademy.back.account.customer.service;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;

public interface CustomerService {

	ResponseCustomerDTO postCustomerLogin(RequestCustomerLoginDTO requestCustomerLoginDTO);

	ResponseCustomerDTO postCustomerRegister(RequestCustomerRegisterDTO requestCustomerRegisterDTO);

	boolean isExistsCustomerEmail(String customerEmail);

}
