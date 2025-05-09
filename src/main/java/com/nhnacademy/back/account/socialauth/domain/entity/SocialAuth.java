package com.nhnacademy.back.account.socialauth.domain.entity;

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
public class SocialAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long socialAuthId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialAuthName socialAuthName;

}
