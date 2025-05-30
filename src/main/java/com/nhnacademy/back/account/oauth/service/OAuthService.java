package com.nhnacademy.back.account.oauth.service;

import java.time.LocalDate;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.repository.MemberStateJpaRepository;
import com.nhnacademy.back.account.oauth.exception.RegisterOAuthFailedException;
import com.nhnacademy.back.account.oauth.model.dto.request.RequestOAuthRegisterDTO;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.repository.SocialAuthJpaRepository;
import com.nhnacademy.back.common.parser.DateParser;
import com.nhnacademy.back.event.event.RegisterPointEvent;
import com.nhnacademy.back.event.event.WelcomeCouponEvent;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthService {

	private final MemberService memberService;

	private final MemberJpaRepository memberJpaRepository;
	private final MemberRankJpaRepository memberRankJpaRepository;
	private final MemberStateJpaRepository memberStateJpaRepository;
	private final MemberRoleJpaRepository memberRoleJpaRepository;
	private final SocialAuthJpaRepository socialAuthJpaRepository;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * OAuth 로그인 전 이미 회원가입을 한 적이 있는지 확인하는 메서드
	 */
	public boolean checkOAuthId(String memberId) {
		return memberService.existsMemberByMemberId(memberId);
	}

	/**
	 * OAuth 계정 회원가입
	 */
	@Transactional
	public void registerOAuth(RequestOAuthRegisterDTO requestOAuthRegisterDTO) {
		Customer customer = Customer.builder()
			.customerEmail(requestOAuthRegisterDTO.getEmail())
			.customerPassword("PAYCO_DUMMY_PASSWORD")
			.customerName(requestOAuthRegisterDTO.getName())
			.build();

		MemberRank memberRank = memberRankJpaRepository.getMemberRankByMemberRankId(1);
		MemberState memberState = memberStateJpaRepository.getMemberStateByMemberStateId(1);
		MemberRole memberRole = memberRoleJpaRepository.getMemberRoleByMemberRoleId(2);
		SocialAuth socialAuth = socialAuthJpaRepository.getSocialAuthBySocialAuthId(2);

		LocalDate birthdayMMdd = DateParser.LocalDateParser(requestOAuthRegisterDTO.getBirthdayMMdd());

		Member member = Member.builder()
			.customer(customer)
			.memberId(requestOAuthRegisterDTO.getMemberId())
			.memberBirth(birthdayMMdd)
			.memberPhone(requestOAuthRegisterDTO.getMobile())
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(null)
			.memberRank(memberRank)
			.memberState(memberState)
			.memberRole(memberRole)
			.socialAuth(socialAuth)
			.build();

		try {
			memberJpaRepository.saveAndFlush(member);

			// 웰컴 쿠폰 발급 이벤트 발행
			eventPublisher.publishEvent(new WelcomeCouponEvent(member.getMemberId()));

			// 회원가입 포인트 적립 이벤트 발행
			eventPublisher.publishEvent(new RegisterPointEvent(member.getMemberId()));
		} catch (Exception ex) {
			throw new RegisterOAuthFailedException("OAuth 회원 가입에 실패했습니다.");
		}
	}

}
