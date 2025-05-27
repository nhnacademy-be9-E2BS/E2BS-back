package com.nhnacademy.back.account.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;

public interface CustomerService {

	Page<ResponseAdminSettingsNonMembersDTO> getAdminSettingsNonMembers(Pageable pageable);

}
