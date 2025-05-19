package com.nhnacademy.back.account.member.service.impl;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.AlreadyExistsMemberIdException;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.repository.MemberStateJpaRepository;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.repository.SocialAuthJpaRepository;
import com.nhnacademy.back.common.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

	@Mock
	private MemberJpaRepository memberJpaRepository;
	@Mock
	private MemberRankJpaRepository memberRankJpaRepository;
	@Mock
	private MemberStateJpaRepository memberStateJpaRepository;
	@Mock
	private MemberRoleJpaRepository memberRoleJpaRepository;
	@Mock
	private SocialAuthJpaRepository socialAuthJpaRepository;

	@InjectMocks
	private MemberServiceImpl memberService;

	@Test
	@DisplayName("ID에 해당하는 회원이 존재하는지 확인")
	void existsMemberByMemberIdTest() {

		// Given
		String memberId = "nhn1";

		// When
		when(memberJpaRepository.existsMemberByMemberId(memberId)).thenReturn(true);

		// Then
		Assertions.assertTrue(memberService.existsMemberByMemberId(memberId));

	}

	@Test
	@DisplayName("ID에 해당하는 회원 정보 가져오는 테스트")
	void getMemberByMemberIdTest() {

		// Given
		String memberId = "nhn1";
		Member member = Member.builder()
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(member);

		Member result = memberService.getMemberByMemberId(memberId);

		// Then
		Assertions.assertNotNull(result);

	}

	@Test
	@DisplayName("RequestLoginMemberDTO 요청을 받지 못했을 경우 예외 처리")
	void requestLoginMemberDTOBadRequestExceptionTest() {

		// Given

		// When

		// Then
		Assertions.assertThrows(BadRequestException.class, () -> {
			memberService.loginMember(null);
		});

	}

	@Test
	@DisplayName("아이디에 해당하는 회원이 존재하지 않는 경우")
	void loginMemberNotExistsMemberByMemberIdTest() {

		// Given
		String memberId = "nhn1";

		RequestLoginMemberDTO requestLoginMemberDTO = new RequestLoginMemberDTO(memberId);

		// When
		when(memberService.existsMemberByMemberId(requestLoginMemberDTO.getMemberId())).thenReturn(false);

		// Then
		Assertions.assertThrows(LoginMemberIsNotExistsException.class, () -> {
			memberService.loginMember(requestLoginMemberDTO);
		});

	}

	@Test
	@DisplayName("아이디에 해당하는 회원 반환")
	void loginMemberReturnMemberTest() {

		// Given
		String memberId = "nhn1";
		RequestLoginMemberDTO requestLoginMemberDTO = new RequestLoginMemberDTO(memberId);

		Member member = Member.builder().build();

		// When
		when(memberService.existsMemberByMemberId(requestLoginMemberDTO.getMemberId())).thenReturn(true);
		when(memberService.getMemberByMemberId(requestLoginMemberDTO.getMemberId())).thenReturn(member);

		// Then
		Assertions.assertNotNull(memberService.loginMember(requestLoginMemberDTO));

	}

	@Test
	@DisplayName("회원가입 시 RequestRegisterMemberDTO 요청을 받지 못했을 경우 예외 처리")
	void registerMemberRequestRegisterMemberDTOBadRequestException() {

		// Given
		RequestRegisterMemberDTO requestRegisterMemberDTO = null;

		// When

		// Then
		Assertions.assertThrows(BadRequestException.class, () -> {
			memberService.registerMember(requestRegisterMemberDTO);
		});

	}

	@Test
	@DisplayName("회원가입 시 아이디가 중복된 경우 예외 처리")
	void registerMemberExistsMemberByMemberIdTest() {

		// Given
		String memberId = "nhn1";
		String customerName = "NHN";
		String customerPassword = "1234";
		String customerPasswordCheck = "1234";
		String customerEmail = "nhn@gmail.com";
		LocalDate memberBirth = LocalDate.now();
		String memberPhone = "01012345678";

		RequestRegisterMemberDTO requestRegisterMemberDTO = new RequestRegisterMemberDTO(
			memberId, customerName, customerPassword, customerPasswordCheck, customerEmail,
			memberBirth, memberPhone
		);

		// When
		when(memberService.existsMemberByMemberId(memberId)).thenReturn(true);

		// Then
		Assertions.assertThrows(AlreadyExistsMemberIdException.class, () -> {
			memberService.registerMember(requestRegisterMemberDTO);
		});

	}

	@Test
	@DisplayName("회원가입 성공 시 memberJpaRepository에 정보 저장")
	void successRegisterMemberTest() {

		// Given
		String memberId = "nhn1";
		String customerName = "NHN";
		String customerPassword = "1234";
		String customerPasswordCheck = "1234";
		String customerEmail = "nhn@gmail.com";
		LocalDate memberBirth = LocalDate.now();
		String memberPhone = "01012345678";

		RequestRegisterMemberDTO requestRegisterMemberDTO = new RequestRegisterMemberDTO(
			memberId, customerName, customerPassword, customerPasswordCheck, customerEmail,
			memberBirth, memberPhone
		);

		MemberRank memberRank = MemberRank.builder().build();
		MemberState memberState = MemberState.builder().build();
		MemberRole memberRole = MemberRole.builder().build();
		SocialAuth socialAuth = SocialAuth.builder().build();

		// When
		when(memberRankJpaRepository.getMemberRankByMemberRankId(1)).thenReturn(memberRank);
		when(memberStateJpaRepository.getMemberStateByMemberStateId(1)).thenReturn(memberState);
		when(memberRoleJpaRepository.getMemberRoleByMemberRoleId(2)).thenReturn(memberRole);
		when(socialAuthJpaRepository.getSocialAuthBySocialAuthId(1)).thenReturn(socialAuth);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> memberService.registerMember(requestRegisterMemberDTO))
			.doesNotThrowAnyException();

	}

}