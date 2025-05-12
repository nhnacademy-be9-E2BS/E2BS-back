package com.nhnacademy.back.product.contributor.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Position {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long positionId;

	@Column(length = 10, nullable = false)
	private String positionName;

	public Position(String positionName) {
		this.positionName = positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
}
