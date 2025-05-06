package com.nhnacademy.back.order.deliveryfee.domain.entity;

import java.time.LocalDate;

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
public class DeliveryFee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long deliveryFeeId;

	@Column(nullable = false)
	private long deliveryFeeAmount;

	@Column(nullable = false)
	private long deliveryFeeFreeAmount;

	@Column(nullable = false)
	private LocalDate deliveryFeeDate;

}
