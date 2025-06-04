package com.nhnacademy.back.account.customer.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long customerId;

	@Column(nullable = false, length = 100)
	private String customerEmail;

	@Column(nullable = false)
	private String customerPassword;

	@Column(nullable = false)
	private String customerName;

	public Customer(String customerEmail, String customerPassword, String customerName) {
		this.customerEmail = customerEmail;
		this.customerPassword = customerPassword;
		this.customerName = customerName;
	}

}
