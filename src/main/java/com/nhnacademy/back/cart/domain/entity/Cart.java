package com.nhnacademy.back.cart.domain.entity;

import java.util.List;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cartId;

	@OneToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "cart")
	private List<CartItems> cartItems;

	public Cart(Customer customer) {
		this.customer = customer;
	}

}
