package com.nhnacademy.back.account.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.member.domain.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {
}
