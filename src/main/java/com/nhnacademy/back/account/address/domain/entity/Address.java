package com.nhnacademy.back.account.address.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.member.domain.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long addressId;

	@Column(nullable = false, length = 5)
	private String addressCode;

	private String addressInfo;

	@Column(nullable = false)
	private String addressDetail;

	@Column(nullable = false)
	private String addressExtra;

	@Column(length = 20)
	private String addressAlias;

	@Column(nullable = false)
	private boolean addressDefault = false;

	@Column(nullable = false)
	private LocalDateTime addressCreatedAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Member member;

	@Column(nullable = false)
	private String addressReceiver;

	@Column(nullable = false)
	private String addressReceiverPhone;

}
