package com.nhnacademy.back.account.socialauth.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SocialAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int socialAuthId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialAuthName socialAuthName;

}
