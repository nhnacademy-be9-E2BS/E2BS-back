package com.nhnacademy.back.account.memberstate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;

public interface MemberStateJpaRepository extends JpaRepository<MemberState,Long> {
}
