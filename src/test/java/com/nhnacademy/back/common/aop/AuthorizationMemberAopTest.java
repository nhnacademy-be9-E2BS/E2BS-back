package com.nhnacademy.back.common.aop;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.AccessDeniedException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.jwt.parser.JwtMemberIdParser;
import com.nhnacademy.back.jwt.rule.JwtRule;

import jakarta.servlet.http.HttpServletRequest;

class AuthorizationMemberAopTest {

	@InjectMocks
	private AuthorizationMemberAop authorizationMemberAop;

	@Mock
	private HttpServletRequest request;

	@Mock
	private MemberJpaRepository memberJpaRepository;

	@Mock
	private ProceedingJoinPoint joinPoint;

	private static final String MEMBER_ID = "user";
	private static final String ACCESS_TOKEN = "header.payload.signature";

	private AutoCloseable mocks;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		mocks.close();
	}

	@Test
	@DisplayName("회원 권한 체크 메서드 테스트")
	void checkAuthorizationMemberAopMethodTest() throws Throwable {

		// Given
		String headerValue = JwtRule.JWT_ISSUE_HEADER.getValue() + "=" + ACCESS_TOKEN;

		when(request.getHeader(JwtRule.JWT_ISSUE_HEADER.getValue())).thenReturn(headerValue);

		try (MockedStatic<JwtMemberIdParser> parserMock = mockStatic(JwtMemberIdParser.class)) {
			parserMock.when(() -> JwtMemberIdParser.getMemberId(ACCESS_TOKEN)).thenReturn(MEMBER_ID);

			MemberRole role = new MemberRole(1L, MemberRoleName.MEMBER);
			Member member = mock(Member.class);

			// When
			when(member.getMemberRole()).thenReturn(role);
			when(memberJpaRepository.getMemberByMemberId(MEMBER_ID)).thenReturn(member);

			when(joinPoint.proceed()).thenReturn("success");

			Object result = authorizationMemberAop.checkAuthorizationMemberAop(joinPoint);

			// Then
			assertThat(result).isEqualTo("success");
			verify(joinPoint).proceed();

		}

	}

	@Test
	@DisplayName("회원 권한 체크 메서드 AccessDeniedException 테스트")
	void checkAuthorizationMemberAopMethodAccessDeniedExceptionTest() throws Throwable {

		// Given
		String headerValue = JwtRule.JWT_ISSUE_HEADER.getValue() + "=" + ACCESS_TOKEN;
		when(request.getHeader(JwtRule.JWT_ISSUE_HEADER.getValue())).thenReturn(headerValue);

		try (MockedStatic<JwtMemberIdParser> parserMock = mockStatic(JwtMemberIdParser.class)) {
			parserMock.when(() -> JwtMemberIdParser.getMemberId(ACCESS_TOKEN)).thenReturn(MEMBER_ID);

			MemberRole role = new MemberRole(2L, MemberRoleName.ADMIN);
			Member member = mock(Member.class);

			// When
			when(member.getMemberRole()).thenReturn(role);
			when(memberJpaRepository.getMemberByMemberId(MEMBER_ID)).thenReturn(member);

			// Then
			Assertions.assertThrows(AccessDeniedException.class, () -> {
				authorizationMemberAop.checkAuthorizationMemberAop(joinPoint);
			});

		}
	}
}