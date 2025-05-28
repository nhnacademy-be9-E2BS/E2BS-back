package com.nhnacademy.back.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.member.repository.MemberJpaRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAdminAop {

	private final HttpServletRequest request;
	private final MemberJpaRepository memberJpaRepository;

	@Around("@annotation(com.nhnacademy.back.common.annotation.Admin)")
	public Object checkAuthorizationAdminAop(ProceedingJoinPoint joinPoint) throws Throwable {
		// String header = request.getHeader(JwtRule.JWT_ISSUE_HEADER.getValue());
		//
		// String accessToken = "";
		// if (Objects.nonNull(header) && header.contains("=")) {
		// 	accessToken = header.substring(header.indexOf("=") + 1).trim();
		// }

		// String memberId = JwtMemberIdParser.getMemberId(accessToken);
		//
		// Member member = memberJpaRepository.getMemberByMemberId(memberId);
		// MemberRoleName memberRoleName = member.getMemberRole().getMemberRoleName();
		//
		// if (memberRoleName != MemberRoleName.ADMIN) {
		// 	throw new AccessDeniedException("관리자만 접근 가능한 페이지입니다.");
		// }

		return joinPoint.proceed();
	}

}
