package com.nhnacademy.back.rank.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long rankId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RankName rankName;

	@Column(nullable = false)
	private int tierBonusRate;

	@Column(nullable = false)
	private int rankRequireAmount;

}
