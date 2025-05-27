package com.nhnacademy.back.account.customer.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerJpaRepository customerJpaRepository;

	@Override
	public Page<ResponseAdminSettingsNonMembersDTO> getAdminSettingsNonMembers(Pageable pageable) {
		Page<Customer> customers = customerJpaRepository.findCustomersNotMembers(pageable);

		return customers.map(customer -> ResponseAdminSettingsNonMembersDTO.builder()
			.customer(customer).build());
	}

}
