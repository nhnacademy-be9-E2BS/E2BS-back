package com.nhnacademy.back.account.address.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.address.domain.dto.UpdateAddressDTO;
import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;
import com.nhnacademy.back.account.address.domain.entity.Address;
import com.nhnacademy.back.account.address.exception.DeleteAddressFailedException;
import com.nhnacademy.back.account.address.exception.NotFoundAddressException;
import com.nhnacademy.back.account.address.exception.SaveAddressFailedException;
import com.nhnacademy.back.account.address.exception.UpdateAddressFailedException;
import com.nhnacademy.back.account.address.repository.AddressJpaRepository;
import com.nhnacademy.back.account.address.service.AddressService;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

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
				.addressReceiver(address.getAddressReceiver())
				.addressReceiverPhone(address.getAddressReceiverPhone())
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
			.addressReceiver(requestMemberAddressSaveDTO.getAddressReceiver())
			.addressReceiverPhone(requestMemberAddressSaveDTO.getAddressReceiverPhone())
			.build();

		Address savedAddress = addressJpaRepository.save(address);
		if (savedAddress.getAddressId() <= 0) {
			throw new SaveAddressFailedException("배송지를 저장히지 못했습니다.");
		}

		privateSetDefaultAddress(memberId, savedAddress.getAddressId());
	}

	public ResponseMemberAddressDTO getAddressByAddressId(String memberId, long addressId) {
		Address address = addressJpaRepository.getAddressByAddressId(addressId);
		if (Objects.isNull(address)) {
			throw new NotFoundAddressException("해당 주소를 찾지 못했습니다.");
		}

		return ResponseMemberAddressDTO.builder()
			.addressId(address.getAddressId())
			.addressCode(address.getAddressCode())
			.addressInfo(address.getAddressInfo())
			.addressDetail(address.getAddressDetail())
			.addressExtra(address.getAddressExtra())
			.addressAlias(address.getAddressAlias())
			.addressDefault(address.isAddressDefault())
			.addressCreatedAt(address.getAddressCreatedAt())
			.addressReceiver(address.getAddressReceiver())
			.addressReceiverPhone(address.getAddressReceiverPhone())
			.build();
	}

	@Transactional
	public void updateAddress(RequestMemberAddressSaveDTO request, String memberId,
		long addressId) {

		UpdateAddressDTO updateAddressDTO = UpdateAddressDTO.builder()
			.addressAlias(request.getAddressAlias())
			.addressCode(request.getAddressCode())
			.addressInfo(request.getAddressInfo())
			.addressDetail(request.getAddressDetail())
			.addressExtra(request.getAddressExtra())
			.addressDefault(request.isAddressDefault())
			.addressCreatedAt(LocalDateTime.now())
			.addressId(addressId)
			.addressReceiver(request.getAddressReceiver())
			.addressReceiverPhone(request.getAddressReceiverPhone())
			.build();

		int result = addressJpaRepository.updateAddress(updateAddressDTO);

		if (result <= 0) {
			throw new UpdateAddressFailedException("배송지 정보를 수정하지 못했습니다.");
		}

		privateSetDefaultAddress(memberId, addressId);
	}

	@Transactional
	public void deleteAddress(String memberId, long addressId) {
		addressJpaRepository.deleteById(addressId);

		if (addressJpaRepository.existsById(addressId)) {
			throw new DeleteAddressFailedException("배송지 정보를 삭제하지 못했습니다.");
		}
	}

	@Transactional
	public void setDefaultAddress(String memberId, long addressId) {
		privateSetDefaultAddress(memberId, addressId);
	}

	private void privateSetDefaultAddress(String memberId, long addressId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);

		int result = addressJpaRepository.updateAllAddressDefaultFalse(member);
		if (result < 0) {
			throw new UpdateAddressFailedException("기본 배송지를 수정하지 못했습니다.");
		}

		int resultDefault = addressJpaRepository.updateAddressDefaultTrue(addressId);
		if (resultDefault < 0) {
			throw new UpdateAddressFailedException("기본 배송지를 수정하지 못했습니다.");
		}
	}

}
