package com.nhnacademy.back.account.memberrole.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;

public interface MemberRoleJpaRepository extends JpaRepository<MemberRole, Long> {

	MemberRole getMemberRoleByMemberRoleId(long memberRoleId);

	MemberRole findMemberRoleByMemberRoleName(MemberRoleName memberRoleName);

}
