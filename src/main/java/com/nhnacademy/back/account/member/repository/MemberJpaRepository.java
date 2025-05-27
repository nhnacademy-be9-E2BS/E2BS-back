package com.nhnacademy.back.account.member.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

	boolean existsMemberByMemberId(String memberId);

	Member getMemberByMemberId(String memberId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberLoginLatest = :memberLoginLatest WHERE m.memberId = :memberId")
	void updateMemberLoginLatestByMemberId(LocalDate memberLoginLatest, String memberId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberBirth = :memberBirth, m.memberPhone = :memberPhone WHERE m.memberId = :memberId")
	int updateMemberInfo(LocalDate memberBirth, String memberPhone, String memberId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberState = :memberState WHERE m.memberId = :memberId")
	int updateMemberMemberState(MemberState memberState, String memberId);

	@Query("SELECT COUNT(m) FROM Member m WHERE m.memberRole.memberRoleName = 'MEMBER' AND m.memberLoginLatest = :today")
	int countTodayLoginMembers(LocalDate today);

	@Query("SELECT COUNT(m) FROM Member m WHERE m.memberRole.memberRoleName = 'MEMBER'")
	int countAllMembers();

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberRole = :memberRole WHERE m.memberId = :memberId")
	int updateMemberRole(MemberRole memberRole, String memberId);

}
