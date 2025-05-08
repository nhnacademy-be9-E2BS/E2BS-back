package com.nhnacademy.back.cart.domain.entity;

import com.nhnacademy.back.product.product.domain.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItems {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cartItemsId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(nullable = false)
	private int cartItemsQuantity;

}
