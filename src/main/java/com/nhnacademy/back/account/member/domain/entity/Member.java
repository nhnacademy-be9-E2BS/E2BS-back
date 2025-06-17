package com.nhnacademy.back.account.member.domain.entity;

import java.time.LocalDate;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
public class Member {

	@Id
	private long customerId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId  // 공유 기본 키 사용
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(nullable = false, length = 50)
	private String memberId;

	@Column(nullable = false)
	private LocalDate memberBirth;

	@Column(nullable = false, length = 13)
	private String memberPhone;

	@Column(nullable = false)
	private LocalDate memberCreatedAt;

	private LocalDate memberLoginLatest;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "member_rank_id")
	private MemberRank memberRank;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "member_state_id")
	private MemberState memberState;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "member_role_id")
	private MemberRole memberRole;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "social_auth_id")
	private SocialAuth socialAuth;

	public void updateStateToDormant(MemberState dormantState) {
		this.memberState = dormantState;
	}

	public void updateMemberRank(MemberRank memberRank) {
		this.memberRank = memberRank;
	}

}
