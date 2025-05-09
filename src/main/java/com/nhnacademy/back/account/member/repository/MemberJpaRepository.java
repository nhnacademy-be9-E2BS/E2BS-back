package com.nhnacademy.back.account.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.member.domain.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {

	boolean existsMemberByMemberId(String memberId);

	Member getMemberByMemberId(String memberId);

}
