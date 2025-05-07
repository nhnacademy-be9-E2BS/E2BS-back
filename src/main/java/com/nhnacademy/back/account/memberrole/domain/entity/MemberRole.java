package com.nhnacademy.back.account.memberrole.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long memberRoleId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberRoleName memberRoleName;

}
