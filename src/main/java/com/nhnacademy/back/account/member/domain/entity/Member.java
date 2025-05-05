package com.nhnacademy.back.account.member.domain.entity;

import java.util.Date;


import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.rank.domain.entity.Rank;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	private long customerId;

	@OneToOne
	@MapsId  // 공유 기본 키 사용
	@JoinColumn(name = "customerId")
	private Customer customer;

	@Column(nullable = false, length = 20)
	private String memberId;

	@Column(nullable = false)
	private Date memberBirth;

	@Column(nullable = false, length = 11)
	private String memberPhone;

	@Column(nullable = false)
	private Date memberCreatedAt;

	private Date memberLoginLatest;

	@ManyToOne(optional = false)
	private Rank rank;

	@ManyToOne(optional = false)
	private MemberState memberState;

	@ManyToOne(optional = false)
	private MemberRole memberRole;

	@ManyToOne(optional = false)
	private SocialAuth socialAuth;

}
