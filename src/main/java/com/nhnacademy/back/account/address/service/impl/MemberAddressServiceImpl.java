package com.nhnacademy.back.account.address.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;
import com.nhnacademy.back.account.address.domain.entity.Address;
import com.nhnacademy.back.account.address.exception.SaveMemberAddressFailedException;
import com.nhnacademy.back.account.address.repository.AddressJpaRepository;
import com.nhnacademy.back.account.address.service.MemberAddressService;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAddressServiceImpl implements MemberAddressService {

	private final AddressJpaRepository addressJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	public List<ResponseMemberAddressDTO> getMemberAddresses(String memberId) {
		List<Address> addresses = addressJpaRepository.getAddressesByMember_MemberId(memberId);

		return addresses.stream()
			.map(address -> ResponseMemberAddressDTO.builder()
				.addressId(address.getAddressId())
				.addressCode(address.getAddressCode())
				.addressInfo(address.getAddressInfo())
				.addressDetail(address.getAddressDetail())
				.addressExtra(address.getAddressExtra())
				.addressAlias(address.getAddressAlias())
				.addressDefault(address.isAddressDefault())
				.addressCreatedAt(address.getAddressCreatedAt())
				.build())
			.collect(Collectors.toList());
	}

	@Transactional
	public void saveMemberAddress(String memberId, RequestMemberAddressSaveDTO requestMemberAddressSaveDTO) {

		Member member = memberJpaRepository.getMemberByMemberId(memberId);

		Address address = Address.builder()
			.addressCode(requestMemberAddressSaveDTO.getAddressCode())
			.addressInfo(requestMemberAddressSaveDTO.getAddressInfo())
			.addressDetail(requestMemberAddressSaveDTO.getAddressDetail())
			.addressExtra(requestMemberAddressSaveDTO.getAddressExtra())
			.addressAlias(requestMemberAddressSaveDTO.getAddressAlias())
			.addressDefault(requestMemberAddressSaveDTO.isAddressDefault())
			.addressCreatedAt(LocalDateTime.now())
			.member(member)
			.build();

		Address savedAddress = addressJpaRepository.save(address);
		if (savedAddress.getAddressId() <= 0) {
			throw new SaveMemberAddressFailedException("배송지를 저장히지 못했습니다.");
		}
	}

}
