package com.nhnacademy.back.account.admin.domain.dto.response;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminSettingsNonMembersDTO {

	private Customer customer;

}
