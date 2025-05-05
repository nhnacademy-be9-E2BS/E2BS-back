package com.nhnacademy.back.account.memberstate.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class MemberState {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long memberStateId;

	@Column(nullable = false)
	private MemberStateName memberStateName;

}
