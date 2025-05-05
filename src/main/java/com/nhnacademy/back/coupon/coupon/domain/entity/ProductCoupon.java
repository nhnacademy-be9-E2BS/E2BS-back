package com.nhnacademy.back.coupon.coupon.domain.entity;

import com.nhnacademy.back.product.product.domain.entity.Product;

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
public class ProductCoupon {

	@Id
	private Long couponId;

	@MapsId
	@OneToOne(optional = false)
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

}
