package com.nhnacademy.back.cart.domain.entity;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cartId;

	@OneToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Cart(Customer customer) {
		this.customer = customer;
	}

}
