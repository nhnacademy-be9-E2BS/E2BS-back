package com.nhnacademy.back.account.oauth.service;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;
import com.nhnacademy.back.account.memberstate.repository.MemberStateJpaRepository;
import com.nhnacademy.back.account.oauth.exception.RegisterOAuthFailedException;
import com.nhnacademy.back.account.oauth.model.dto.request.RequestOAuthRegisterDTO;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuthName;
import com.nhnacademy.back.account.socialauth.repository.SocialAuthJpaRepository;
import com.nhnacademy.back.event.event.RegisterPointEvent;
import com.nhnacademy.back.event.event.WelcomeCouponEvent;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

	@InjectMocks
	private OAuthService oAuthService;

	@Mock
	private MemberService memberService;

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
	private ApplicationEventPublisher applicationEventPublisher;

	@Test
	@DisplayName("OAuth 로그인 전 회원인지 체크 메서드 테스트")
	void checkOAuthIdMethodTest() {

		// Given

		// When
		when(memberService.existsMemberByMemberId("user")).thenReturn(true);

		// Then
		Assertions.assertThatCode(() -> {
			oAuthService.checkOAuthId("user");
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("OAuth 계정 회원가입 메서드 테스트")
	void registerOAuthMethodTest() {

		// Given
		RequestOAuthRegisterDTO requestOAuthRegisterDTO = new RequestOAuthRegisterDTO(
			"user", "user@naver.com", "010-1234-1234", "user", "0101"
		);

		// When
		MemberRank memberRank = new MemberRank(1L, RankName.NORMAL, 1, 1L);
		MemberState memberState = new MemberState(1L, MemberStateName.ACTIVE);
		MemberRole memberRole = new MemberRole(1L, MemberRoleName.MEMBER);
		SocialAuth socialAuth = new SocialAuth(1L, SocialAuthName.WEB);

		when(memberRankJpaRepository.getMemberRankByMemberRankId(1)).thenReturn(memberRank);
		when(memberStateJpaRepository.getMemberStateByMemberStateId(1L)).thenReturn(memberState);
		when(memberRoleJpaRepository.getMemberRoleByMemberRoleId(2L)).thenReturn(memberRole);
		when(socialAuthJpaRepository.getSocialAuthBySocialAuthId(2L)).thenReturn(socialAuth);

		when(memberJpaRepository.saveAndFlush(any(Member.class))).thenReturn(null);
		doNothing().when(applicationEventPublisher).publishEvent(any(WelcomeCouponEvent.class));
		doNothing().when(applicationEventPublisher).publishEvent(any(RegisterPointEvent.class));

		// Then
		Assertions.assertThatCode(() -> {
			oAuthService.registerOAuth(requestOAuthRegisterDTO);
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("OAuth 계정 회원가입 메서드 RegisterOAuthFailedException 테스트")
	void registerOAuthMethodRegisterOAuthFailedExceptionTest() {

		// Given
		RequestOAuthRegisterDTO requestOAuthRegisterDTO = new RequestOAuthRegisterDTO(
			"user", "user@naver.com", "010-1234-1234", "user", "0101"
		);

		// When
		MemberRank memberRank = new MemberRank(1L, RankName.NORMAL, 1, 1L);
		MemberState memberState = new MemberState(1L, MemberStateName.ACTIVE);
		MemberRole memberRole = new MemberRole(1L, MemberRoleName.MEMBER);
		SocialAuth socialAuth = new SocialAuth(1L, SocialAuthName.WEB);

		when(memberRankJpaRepository.getMemberRankByMemberRankId(1)).thenReturn(memberRank);
		when(memberStateJpaRepository.getMemberStateByMemberStateId(1L)).thenReturn(memberState);
		when(memberRoleJpaRepository.getMemberRoleByMemberRoleId(2L)).thenReturn(memberRole);
		when(socialAuthJpaRepository.getSocialAuthBySocialAuthId(2L)).thenReturn(socialAuth);

		when(memberJpaRepository.saveAndFlush(any(Member.class))).thenReturn(null);
		doNothing().when(applicationEventPublisher).publishEvent(any(WelcomeCouponEvent.class));
		doNothing().when(applicationEventPublisher).publishEvent(any());

		// Then
		org.junit.jupiter.api.Assertions.assertThrows(RegisterOAuthFailedException.class, () -> {
			oAuthService.registerOAuth(requestOAuthRegisterDTO);
		});

	}

}