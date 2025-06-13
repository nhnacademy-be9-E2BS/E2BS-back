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
import org.springframework.context.ApplicationEventPublisher;

import com.nhnacademy.back.account.admin.domain.dto.request.RequestAdminSettingsMemberStateDTO;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.dto.MemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberIdDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberInfoDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.AlreadyExistsMemberIdException;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.exception.MemberStateWithdrawException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberStateException;
import com.nhnacademy.back.account.member.exception.UpdateMemberInfoFailedException;
import com.nhnacademy.back.account.member.exception.UpdateMemberRoleFailedException;
import com.nhnacademy.back.account.member.exception.UpdateMemberStateFailedException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;
import com.nhnacademy.back.account.memberstate.repository.MemberStateJpaRepository;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuthName;
import com.nhnacademy.back.account.socialauth.repository.SocialAuthJpaRepository;

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
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Mock
	private CustomerJpaRepository customerJpaRepository;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

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
	@DisplayName("정상 로그인 테스트")
	void loginMember_Success() {
		// Given
		RequestLoginMemberDTO requestDTO = new RequestLoginMemberDTO("user");

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(1L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberService.existsMemberByMemberId("user")).thenReturn(true);
		when(memberService.getMemberByMemberId("user")).thenReturn(member);

		MemberDTO result = memberService.loginMember(requestDTO);

		// Then
		org.assertj.core.api.Assertions.assertThat(result.getMemberId()).isEqualTo("user");
		org.assertj.core.api.Assertions.assertThat(result.getCustomer().getCustomerName()).isEqualTo("김도윤");

		verify(memberJpaRepository, times(1))
			.updateMemberLoginLatestByMemberId(any(LocalDate.class), eq("user"));
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
		Assertions.assertThrows(LoginMemberIsNotExistsException.class,
			() -> memberService.loginMember(requestLoginMemberDTO));

	}

	@Test
	@DisplayName("로그인 실패 - 탈퇴한 회원")
	void loginMember_WithdrawnMember() {

		// Given
		RequestLoginMemberDTO requestLoginMemberDTO = new RequestLoginMemberDTO("user");

		MemberState withdrawnState = new MemberState(1L, MemberStateName.WITHDRAW);
		Member member = Member.builder()
			.memberId("user")
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberState(withdrawnState)
			.build();

		// When
		when(memberService.existsMemberByMemberId("user")).thenReturn(true);
		when(memberService.getMemberByMemberId("user")).thenReturn(member);

		// Then
		Assertions.assertThrows(MemberStateWithdrawException.class,
			() -> memberService.loginMember(requestLoginMemberDTO));

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
		Assertions.assertThrows(AlreadyExistsMemberIdException.class,
			() -> memberService.registerMember(requestRegisterMemberDTO));

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

	@Test
	@DisplayName("회원 정보를 가져오는 메서드 테스트")
	void getMemberInfoMethodTest() {

		// Given
		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(1L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.getMemberInfo(new RequestMemberIdDTO("user"));
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 정보를 가져오는 메서드 NotFoundMemberException 테스트")
	void getMemberInfoMethodNotFoundMemberExceptionTest() {

		// Given

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.getMemberInfo(new RequestMemberIdDTO("user")));

	}

	@Test
	@DisplayName("회원 정보 수정 메서드 테스트")
	void updateMemberInfoMethodTest() {

		// Given
		RequestMemberInfoDTO requestMemberInfoDTO = new RequestMemberInfoDTO(
			"user", "user", "user@naver.com", LocalDate.now(), "010-1234-5678",
			"1234", "1234"
		);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(1L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberJpaRepository.updateMemberInfo(
			requestMemberInfoDTO.getMemberBirth(), requestMemberInfoDTO.getMemberPhone(),
			requestMemberInfoDTO.getMemberId()
		)).thenReturn(1);
		when(customerJpaRepository.updateCustomerNameAndCustomerEmail(
			requestMemberInfoDTO.getCustomerName(),
			requestMemberInfoDTO.getCustomerEmail(),
			requestMemberInfoDTO.getCustomerPassword(),
			member.getCustomer().getCustomerId()
		)).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.updateMemberInfo(requestMemberInfoDTO);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 정보 수정 메서드 NotFoundMemberException 테스트")
	void updateMemberInfoMethodNotFoundMemberExceptionTest() {

		// Given
		RequestMemberInfoDTO requestMemberInfoDTO = new RequestMemberInfoDTO(
			"user", "user", "user@naver.com", LocalDate.now(), "010-1234-5678",
			"1234", "1234"
		);

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.updateMemberInfo(requestMemberInfoDTO));

	}

	@Test
	@DisplayName("회원 정보 수정 메서드 UpdateMemberInfoFailedException 테스트")
	void updateMemberInfoMethodUpdateMemberInfoFailedExceptionTest() {

		// Given
		RequestMemberInfoDTO requestMemberInfoDTO = new RequestMemberInfoDTO(
			"user", "user", "user@naver.com", LocalDate.now(), "010-1234-5678",
			"1234", "1234"
		);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(1L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberJpaRepository.updateMemberInfo(
			requestMemberInfoDTO.getMemberBirth(), requestMemberInfoDTO.getMemberPhone(),
			requestMemberInfoDTO.getMemberId()
		)).thenReturn(0);

		// Then
		Assertions.assertThrows(UpdateMemberInfoFailedException.class,
			() -> memberService.updateMemberInfo(requestMemberInfoDTO));

	}

	@Test
	@DisplayName("회원 정보 수정 메서드 UpdateMemberInfoFailedException2 테스트")
	void updateMemberInfoMethodUpdateMemberInfoFailedException2Test() {

		// Given
		RequestMemberInfoDTO requestMemberInfoDTO = new RequestMemberInfoDTO(
			"user", "user", "user@naver.com", LocalDate.now(), "010-1234-5678",
			"1234", "1234"
		);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(1L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberJpaRepository.updateMemberInfo(
			requestMemberInfoDTO.getMemberBirth(), requestMemberInfoDTO.getMemberPhone(),
			requestMemberInfoDTO.getMemberId()
		)).thenReturn(1);
		when(customerJpaRepository.updateCustomerNameAndCustomerEmail(
			requestMemberInfoDTO.getCustomerName(),
			requestMemberInfoDTO.getCustomerEmail(),
			requestMemberInfoDTO.getCustomerPassword(),
			member.getCustomer().getCustomerId()
		)).thenReturn(0);

		// Then
		Assertions.assertThrows(UpdateMemberInfoFailedException.class,
			() -> memberService.updateMemberInfo(requestMemberInfoDTO));

	}

	@Test
	@DisplayName("마이페이지 회원 탈퇴 메서드 테스트")
	void withdrawMemberMethodTest() {

		// Given
		MemberState memberState = new MemberState(3L, MemberStateName.WITHDRAW); // 탈퇴 상태

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(memberState)
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberStateJpaRepository.getMemberStateByMemberStateId(anyLong())).thenReturn(memberState);
		when(memberJpaRepository.updateMemberMemberState(memberState, "user")).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> memberService.withdrawMember("user"))
			.doesNotThrowAnyException();

	}

	@Test
	@DisplayName("마이페이지 회원 탈퇴 메서드 NotFoundMemberException 테스트")
	void withdrawMemberMethodNotFoundMemberExceptionTest() {

		// Given

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.withdrawMember("user"));

	}

	@Test
	@DisplayName("마이페이지 회원 탈퇴 메서드 UpdateMemberStateFailedException 테스트")
	void withdrawMemberMethodUpdateMemberStateFailedExceptionTest() {

		// Given
		MemberState memberState = new MemberState(3L, MemberStateName.WITHDRAW); // 탈퇴 상태

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(memberState)
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberStateJpaRepository.getMemberStateByMemberStateId(anyLong())).thenReturn(memberState);
		when(memberJpaRepository.updateMemberMemberState(memberState, "user")).thenReturn(0);

		// Then
		Assertions.assertThrows(UpdateMemberStateFailedException.class,
			() -> memberService.withdrawMember("user"));

	}

	@Test
	@DisplayName("총 회원 수 조회 메서드 테스트")
	void getTotalMemberCntMethodTest() {

		// Given

		// When
		when(memberJpaRepository.countAllMembers()).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.getTotalMemberCnt();
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("오늘 로그인한 회원 수 조회 메서드 테스트")
	void getTotalTodayLoginMemberCnt() {

		// Given

		// When
		when(memberJpaRepository.countTodayLoginMembers(LocalDate.now())).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.getTotalTodayLoginMembersCnt();
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("관리자 페이지 회원 상태 변경 메서드 테스트")
	void updateMemberStateMethodTest() {

		// Given
		MemberState memberState = new MemberState(3L, MemberStateName.ACTIVE);

		RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO = new RequestAdminSettingsMemberStateDTO(
			"ACTIVE"
		);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(memberState)
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberStateJpaRepository.findMemberStateByMemberStateName(MemberStateName.ACTIVE))
			.thenReturn(memberState);
		when(memberJpaRepository.updateMemberMemberState(memberState, "user")).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.updateMemberState("user", requestAdminSettingsMemberStateDTO);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("관리자 페이지 회원 상태 변경 메서드 NotFoundMemberException 테스트")
	void updateMemberStateMethodNotFoundMemberExceptionTest() {

		// Given
		RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO = new RequestAdminSettingsMemberStateDTO(
			"ACTIVE"
		);

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.updateMemberState("user", requestAdminSettingsMemberStateDTO));

	}

	@Test
	@DisplayName("관리자 페이지 회원 상태 변경 메서드 UpdateMemberStateFailedException 테스트")
	void updateMemberStateMethodUpdateMemberStateFailedExceptionTest() {

		// Given
		MemberState memberState = new MemberState(3L, MemberStateName.ACTIVE);

		RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO = new RequestAdminSettingsMemberStateDTO(
			"ACTIVE"
		);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(memberState)
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberStateJpaRepository.findMemberStateByMemberStateName(MemberStateName.ACTIVE))
			.thenReturn(memberState);
		when(memberJpaRepository.updateMemberMemberState(memberState, "user")).thenReturn(0);

		// Then
		Assertions.assertThrows(UpdateMemberStateFailedException.class,
			() -> memberService.updateMemberState("user", requestAdminSettingsMemberStateDTO));

	}

	@Test
	@DisplayName("관리자 페이지 회원 권한 변경 메서드 테스트")
	void updateMemberRoleMethodTest() {

		// Given
		MemberRole memberRole = new MemberRole(1, MemberRoleName.MEMBER);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(3L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(memberRole)
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberRoleJpaRepository.getMemberRoleByMemberRoleId(1L)).thenReturn(memberRole);
		when(memberJpaRepository.updateMemberRole(memberRole, "user")).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.updateMemberRole("user");
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("관리자 페이지 회원 권한 변경 메서드 NotFoundMemberException 테스트")
	void updateMemberRoleMethodNotFoundMemberExceptionTest() {

		// Given

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.updateMemberRole("user"));

	}

	@Test
	@DisplayName("관리자 페이지 회원 권한 변경 메서드 UpdateMemberRoleFailedException 테스트")
	void updateMemberRoleMethodUpdateMemberRoleFailedExceptionTest() {

		// Given
		MemberRole memberRole = new MemberRole(1, MemberRoleName.MEMBER);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(3L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(memberRole)
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);
		when(memberRoleJpaRepository.getMemberRoleByMemberRoleId(1L)).thenReturn(memberRole);
		when(memberJpaRepository.updateMemberRole(memberRole, "user")).thenReturn(0);

		// Then
		Assertions.assertThrows(UpdateMemberRoleFailedException.class,
			() -> memberService.updateMemberRole("user"));

	}

	@Test
	@DisplayName("회원 상태 변경 메서드 테스트")
	void getMemberStateMethodTest() {

		// Given
		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(3L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.getMemberState("user");
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 상태 변경 메서드 NotFoundMemberException 테스트")
	void getMemberStateMethodNotFoundMemberExceptionTest() {

		// Given

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.getMemberState("user"));

	}

	@Test
	@DisplayName("회원 휴면 상태 변경 메서드 테스트")
	void changeDormantMemberStateActiveMethodTest() {

		// Given
		MemberState memberState = new MemberState(1L, MemberStateName.ACTIVE);

		// When
		when(memberStateJpaRepository.getMemberStateByMemberStateId(1L)).thenReturn(memberState);
		when(memberJpaRepository.updateMemberMemberState(memberState, "user")).thenReturn(1);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.changeDormantMemberStateActive("user");
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 휴면 상태 변경 메서드 NotFoundMemberStateException 테스트")
	void changeDormantMemberStateActiveMethodNotFoundMemberStateExceptionTest() {

		// Given

		// When
		when(memberStateJpaRepository.getMemberStateByMemberStateId(1L)).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberStateException.class,
			() -> memberService.changeDormantMemberStateActive("user"));

	}

	@Test
	@DisplayName("회원 휴면 상태 변경 메서드 UpdateMemberStateFailedException 테스트")
	void changeDormantMemberStateActiveMethodUpdateMemberStateFailedExceptionTest() {

		// Given
		MemberState memberState = new MemberState(1L, MemberStateName.ACTIVE);

		// When
		when(memberStateJpaRepository.getMemberStateByMemberStateId(1L)).thenReturn(memberState);
		when(memberJpaRepository.updateMemberMemberState(memberState, "user")).thenReturn(0);

		// Then
		Assertions.assertThrows(UpdateMemberStateFailedException.class,
			() -> memberService.changeDormantMemberStateActive("user"));

	}

	@Test
	@DisplayName("회원 이메일 조회 메서드 테스트")
	void getMemberEmailMethodTest() {

		// Given
		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@test.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-1234")
			.memberCreatedAt(LocalDate.now())
			.memberState(new MemberState(3L, MemberStateName.ACTIVE))
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1L))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(member);

		// Then
		org.assertj.core.api.Assertions.assertThatCode(() -> {
			memberService.getMemberEmail("user");
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("회원 이메일 조회 메서드 NotFoundMemberException 테스트")
	void getMemberEmailMethodNotFoundMemberExceptionTest() {

		// Given

		// When
		when(memberJpaRepository.getMemberByMemberId("user")).thenReturn(null);

		// Then
		Assertions.assertThrows(NotFoundMemberException.class,
			() -> memberService.getMemberEmail("user"));

	}

}