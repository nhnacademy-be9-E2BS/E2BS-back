package com.nhnacademy.back.account.address.service.impl;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.address.domain.dto.UpdateAddressDTO;
import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.domain.entity.Address;
import com.nhnacademy.back.account.address.exception.DeleteAddressFailedException;
import com.nhnacademy.back.account.address.exception.NotFoundAddressException;
import com.nhnacademy.back.account.address.exception.SaveAddressFailedException;
import com.nhnacademy.back.account.address.exception.UpdateAddressFailedException;
import com.nhnacademy.back.account.address.repository.AddressJpaRepository;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuthName;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

	@InjectMocks
	private AddressServiceImpl addressService;

	@Mock
	private AddressJpaRepository addressJpaRepository;

	@Mock
	private MemberJpaRepository memberJpaRepository;

	@Test
	@DisplayName("회원 주소 목록 조회 메서드 테스트")
	void getMemberAddressesMethodTest() {

		// Given
		Member member = Member.builder()
			.customerId(1)
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		Address address = Address.builder()
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressAlias("우리집")
			.addressDefault(true)
			.addressCreatedAt(LocalDateTime.now())
			.member(member)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		List<Address> addresses = List.of(
			address
		);

		// When
		when(addressJpaRepository.getAddressesByMember_MemberId("user")).thenReturn(addresses);

		// Then
		Assertions.assertThatCode(() -> {
			addressService.getMemberAddresses("user");
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 배송지 저장 메서드 테스트")
	void saveMemberAddressMethodTest() {

		// Given
		Member member = Member.builder()
			.customerId(1)
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		Address address = Address.builder()
			.addressId(1L)
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressAlias("우리집")
			.addressDefault(true)
			.addressCreatedAt(LocalDateTime.now())
			.member(member)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		RequestMemberAddressSaveDTO requestMemberAddressSaveDTO = RequestMemberAddressSaveDTO.builder()
			.addressAlias("우리집")
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressDefault(true)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(addressJpaRepository.save(any(Address.class))).thenReturn(address);

		// Then
		Assertions.assertThatCode(() -> {
			addressService.saveMemberAddress("user", requestMemberAddressSaveDTO);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 배송지 저장 메서드 SaveAddressFailedException 테스트")
	void saveMemberAddressMethodSaveAddressFailedExceptionTest() {

		// Given
		Member member = Member.builder()
			.customerId(1)
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		Address address = Address.builder()
			.addressId(0)
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트 7동")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressAlias("우리집")
			.addressDefault(true)
			.addressCreatedAt(LocalDateTime.now())
			.member(member)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		RequestMemberAddressSaveDTO requestMemberAddressSaveDTO = RequestMemberAddressSaveDTO.builder()
			.addressAlias("우리집")
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트 7동")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressDefault(true)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(addressJpaRepository.save(any(Address.class))).thenReturn(address);

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(SaveAddressFailedException.class, () -> {
			addressService.saveMemberAddress("user", requestMemberAddressSaveDTO);
		});

	}

	@Test
	@DisplayName("배송지 주소 조회 메서드 테스트")
	void getAddressByAddressIdMethodTest() {

		// Given
		Member member = Member.builder()
			.customerId(1)
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberId("user001")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		Address address = Address.builder()
			.addressId(1L)
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트 7동")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressAlias("우리집")
			.addressDefault(true)
			.addressCreatedAt(LocalDateTime.now())
			.member(member)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		// When
		when(addressJpaRepository.getAddressByAddressId(1L)).thenReturn(address);

		// Then
		Assertions.assertThatCode(() -> {
			addressService.getAddressByAddressId("user", 1L);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("배송지 주소 조회 메서드 NotFoundAddressException 테스트")
	void getAddressByAddressIdMethodNotFoundAddressExceptionTest() {

		// Given

		// When
		when(addressJpaRepository.getAddressByAddressId(1L)).thenReturn(null);

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(NotFoundAddressException.class, () -> {
			addressService.getAddressByAddressId("user", 1L);
		});

	}

	@Test
	@DisplayName("배송지 주소 수정 메서드 테스트")
	void updateAddressMethodTest() {

		// Given
		RequestMemberAddressSaveDTO requestMemberAddressSaveDTO = RequestMemberAddressSaveDTO.builder()
			.addressAlias("회사")
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDefault(true)
			.addressDetail("우성아파트 7동")
			.addressExtra("1234#")
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-1234")
			.build();

		// When
		when(addressJpaRepository.updateAddress(any(UpdateAddressDTO.class))).thenReturn(1);

		// Then
		Assertions.assertThatCode(() -> {
			addressService.updateAddress(requestMemberAddressSaveDTO, "user", 1L);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("배송지 주소 수정 메서드 UpdateAddressFailedException 테스트")
	void updateAddressMethodUpdateAddressFailedExceptionTest() {

		// Given
		RequestMemberAddressSaveDTO requestMemberAddressSaveDTO = RequestMemberAddressSaveDTO.builder()
			.addressAlias("회사")
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDefault(true)
			.addressDetail("우성아파트 7동")
			.addressExtra("1234#")
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-1234")
			.build();

		// When
		when(addressJpaRepository.updateAddress(any(UpdateAddressDTO.class))).thenReturn(0);

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(UpdateAddressFailedException.class, () -> {
			addressService.updateAddress(requestMemberAddressSaveDTO, "user", 1L);
		});

	}

	@Test
	@DisplayName("배송지 삭제 메서드 테스트")
	void deleteAddressMethodTest() {

		// Given

		// When
		doNothing().when(addressJpaRepository).deleteById(1L);
		when(addressJpaRepository.existsById(1L)).thenReturn(false);

		// Then
		Assertions.assertThatCode(() -> {
			addressService.deleteAddress("user", 1L);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("배송지 삭제 메서드 DeleteAddressFailedException 테스트")
	void deleteAddressMethodDeleteAddressFailedExceptionTest() {

		// Given

		// When
		doNothing().when(addressJpaRepository).deleteById(1L);
		when(addressJpaRepository.existsById(1L)).thenReturn(true);

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(DeleteAddressFailedException.class, () -> {
			addressService.deleteAddress("user", 1L);
		});

	}

	@Test
	@DisplayName("기본 배송지 설정 메서드 테스트")
	void setDefaultAddressMethodTest() {

		// Given
		Member member = Member.builder()
			.customerId(1L)
			.customer(new Customer("user@naver.com", "1234", "김도윤"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 0))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(addressJpaRepository.updateAllAddressDefaultFalse(member)).thenReturn(1);
		when(addressJpaRepository.updateAddressDefaultTrue(1L)).thenReturn(1);

		// Then
		Assertions.assertThatCode(() -> {
			addressService.setDefaultAddress("user", 1L);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("기본 배송지 설정 메서드 UpdateAddressFailedException 테스트")
	void setDefaultAddressMethodUpdateAddressFailedExceptionTest() {

		// Given
		Member member = Member.builder()
			.customerId(1L)
			.customer(new Customer("user@naver.com", "1234", "김도윤"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 0))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(addressJpaRepository.updateAllAddressDefaultFalse(member)).thenReturn(-1);

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(UpdateAddressFailedException.class, () -> {
			addressService.setDefaultAddress("user", 1L);
		});

	}

	@Test
	@DisplayName("기본 배송지 설정 메서드 UpdateAddressFailedException2 테스트")
	void setDefaultAddressMethodUpdateAddressFailedException2Test() {

		// Given
		Member member = Member.builder()
			.customerId(1L)
			.customer(new Customer("user@naver.com", "1234", "김도윤"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 0))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(addressJpaRepository.updateAllAddressDefaultFalse(member)).thenReturn(1);
		when(addressJpaRepository.updateAddressDefaultTrue(1L)).thenReturn(-1);

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(UpdateAddressFailedException.class, () -> {
			addressService.setDefaultAddress("user", 1L);
		});

	}

}