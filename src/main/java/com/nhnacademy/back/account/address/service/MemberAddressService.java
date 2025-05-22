package com.nhnacademy.back.account.address.service;

import java.util.List;

import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;

public interface MemberAddressService {

	List<ResponseMemberAddressDTO> getMemberAddresses(String memberId);

	void saveMemberAddress(String memberId, RequestMemberAddressSaveDTO requestMemberAddressSaveDTO);

}
