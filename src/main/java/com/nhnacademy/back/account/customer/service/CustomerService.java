package com.nhnacademy.back.account.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerRegisterDTO;

public interface CustomerService {

	Page<ResponseAdminSettingsNonMembersDTO> getAdminSettingsNonMembers(Pageable pageable);

	ResponseCustomerLoginDTO postCustomerLogin(RequestCustomerLoginDTO requestCustomerLoginDTO);

	ResponseCustomerRegisterDTO postCustomerRegister(RequestCustomerRegisterDTO requestCustomerRegisterDTO);

	boolean isExistsCustomerEmail(String customerEmail);
}
