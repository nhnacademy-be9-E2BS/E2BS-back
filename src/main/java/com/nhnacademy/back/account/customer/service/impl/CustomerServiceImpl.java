package com.nhnacademy.back.account.customer.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerEmailAlreadyExistsException;
import com.nhnacademy.back.account.customer.exception.CustomerEmailNotExistsException;
import com.nhnacademy.back.account.customer.exception.CustomerPasswordNotMatchException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerJpaRepository customerJpaRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public Page<ResponseAdminSettingsNonMembersDTO> getAdminSettingsNonMembers(Pageable pageable) {
		Page<Customer> customers = customerJpaRepository.findCustomersNotMembers(pageable);

		return customers.map(customer -> ResponseAdminSettingsNonMembersDTO.builder()
			.customer(customer).build());
	}

	/**
	 * 비회원 로그인 메소드
	 */
	@Override
	public ResponseCustomerDTO postCustomerLogin(RequestCustomerLoginDTO requestCustomerLoginDTO) {
		if (!customerJpaRepository.existsCustomerByCustomerEmail(requestCustomerLoginDTO.getCustomerEmail())) {
			throw new CustomerEmailNotExistsException("이메일이 존재하지 않습니다.");
		}

		Customer customer = customerJpaRepository.getCustomerByCustomerEmail(
			requestCustomerLoginDTO.getCustomerEmail());
		if (!passwordEncoder.matches(requestCustomerLoginDTO.getCustomerPassword(), customer.getCustomerPassword())) {
			throw new CustomerPasswordNotMatchException("비밀번호가 일치하지 않습니다.");
		}

		return new ResponseCustomerDTO(customer.getCustomerName(), customer.getCustomerId());
	}

	/**
	 * 비회원 등록 메소드
	 */
	@Transactional
	@Override
	public ResponseCustomerDTO postCustomerRegister(RequestCustomerRegisterDTO requestCustomerRegisterDTO) {
		if (customerJpaRepository.existsCustomerByCustomerEmail(requestCustomerRegisterDTO.getCustomerEmail())) {
			throw new CustomerEmailAlreadyExistsException("비회원 이메일이 이미 존재합니다.");
		}

		Customer customer = Customer.createCustomerEntity(requestCustomerRegisterDTO);
		customerJpaRepository.save(customer);

		return new ResponseCustomerDTO(customer.getCustomerName(), customer.getCustomerId());
	}

	/**
	 * 중복 아이디 검증 메소드
	 */
	public boolean isExistsCustomerEmail(String customerEmail) {
		return customerJpaRepository.existsCustomerByCustomerEmail(customerEmail);
	}

}
