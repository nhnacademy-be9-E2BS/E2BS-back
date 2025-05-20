package com.nhnacademy.back.account.member.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.account.member.domain.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

	boolean existsMemberByMemberId(String memberId);

	Member getMemberByMemberId(String memberId);

	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.memberLoginLatest = :memberLoginLatest where m.memberId = :memberId")
	void updateMemberLoginLatestByMemberId(LocalDate memberLoginLatest, String memberId);

}
