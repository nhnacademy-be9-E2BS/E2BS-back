package com.nhnacademy.back.account.memberrank.domain.entity;

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
public class MemberRank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long memberRankId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RankName memberRankName;

	@Column(nullable = false)
	private int memberRankTierBonusRate;

	@Column(nullable = false)
	private long memberRankRequireAmount;

}
