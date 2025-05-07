package com.nhnacademy.back.order.orderstate.domain.entity;

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
public class OrderState {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderStateId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStateName orderStateName;

}
