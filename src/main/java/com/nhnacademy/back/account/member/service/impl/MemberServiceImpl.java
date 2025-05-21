package com.nhnacademy.back.account.member.service.impl;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.domain.dto.MemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberIdDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberInfoDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.AlreadyExistsMemberIdException;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.repository.MemberStateJpaRepository;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.repository.SocialAuthJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberJpaRepository memberJpaRepository;
	private final MemberRankJpaRepository memberRankJpaRepository;
	private final MemberStateJpaRepository memberStateJpaRepository;
	private final MemberRoleJpaRepository memberRoleJpaRepository;
	private final SocialAuthJpaRepository socialAuthJpaRepository;

	/**
	 * 아이디에 해당하는 Member가 존재하는지 확인하는 메서드
	 */
	@Override
	public boolean existsMemberByMemberId(String memberId) {
		return memberJpaRepository.existsMemberByMemberId(memberId);
	}

	/**
	 * 해당 아이디를 사용하는 회원 정보 가져오는 메서드
	 */
	@Override
	public Member getMemberByMemberId(String memberId) {
		return memberJpaRepository.getMemberByMemberId(memberId);
	}

	/**
	 * ID에 해당하는 회원이 존재하는지 확인하고 회원 정보 반환
	 */
	@Transactional
	@Override
	public MemberDTO loginMember(RequestLoginMemberDTO requestLoginMemberDTO) {
		if (!existsMemberByMemberId(requestLoginMemberDTO.getMemberId())) {
			throw new LoginMemberIsNotExistsException("아이디에 해당하는 회원이 존재하지 않습니다.");
		}

		Member member = getMemberByMemberId(requestLoginMemberDTO.getMemberId());
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		LocalDate loginLatest = LocalDate.now();
		memberJpaRepository.updateMemberLoginLatestByMemberId(loginLatest, member.getMemberId());

		return new MemberDTO(member.getCustomer(), member.getMemberId(), member.getMemberBirth(),
			member.getMemberPhone(), member.getMemberCreatedAt(), loginLatest,
			member.getMemberRank(), member.getMemberState(), member.getMemberRole(), member.getSocialAuth());
	}

	/**
	 * 회원가입 시 입력한 아이디 값이 이미 존재하는지 확인
	 * 없다면 member 테이블에 회원 저장
	 */
	@Transactional
	@Override
	public ResponseRegisterMemberDTO registerMember(RequestRegisterMemberDTO requestRegisterMemberDTO) {

		if (existsMemberByMemberId(requestRegisterMemberDTO.getMemberId())) {
			throw new AlreadyExistsMemberIdException("중복된 아이디입니다.");
		}

		Customer customer = Customer.builder()
			.customerEmail(requestRegisterMemberDTO.getCustomerEmail())
			.customerPassword(requestRegisterMemberDTO.getCustomerPassword())
			.customerName(requestRegisterMemberDTO.getCustomerName())
			.build();

		MemberRank memberRank = memberRankJpaRepository.getMemberRankByMemberRankId(1);
		MemberState memberState = memberStateJpaRepository.getMemberStateByMemberStateId(1);
		MemberRole memberRole = memberRoleJpaRepository.getMemberRoleByMemberRoleId(2);
		SocialAuth socialAuth = socialAuthJpaRepository.getSocialAuthBySocialAuthId(1);

		Member member = Member.builder()
			.customer(customer)
			.memberId(requestRegisterMemberDTO.getMemberId())
			.memberBirth(requestRegisterMemberDTO.getMemberBirth())
			.memberPhone(requestRegisterMemberDTO.getMemberPhone())
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(null)
			.memberRank(memberRank)
			.memberState(memberState)
			.memberRole(memberRole)
			.socialAuth(socialAuth)
			.build();

		memberJpaRepository.saveAndFlush(member);

		return new ResponseRegisterMemberDTO(
			member.getMemberId(), member.getCustomer().getCustomerName(), member.getCustomer().getCustomerPassword(),
			member.getCustomer().getCustomerEmail(), member.getMemberBirth(), member.getMemberPhone()
		);
	}

	@Override
	public ResponseMemberInfoDTO getMemberInfo(RequestMemberIdDTO requestMemberIdDTO) {
		Member member = memberJpaRepository.getMemberByMemberId(requestMemberIdDTO.getMemberId());
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		return new ResponseMemberInfoDTO(
			member.getCustomer(), member.getMemberId(), member.getMemberBirth(),
			member.getMemberPhone(), member.getMemberCreatedAt(), member.getMemberLoginLatest(),
			member.getMemberRank(), member.getMemberState(), member.getMemberRole(),
			member.getSocialAuth()
		);
	}

}
