package com.nhnacademy.back.account.memberstate.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class MemberState {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long memberStateId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberStateName memberStateName;

}
