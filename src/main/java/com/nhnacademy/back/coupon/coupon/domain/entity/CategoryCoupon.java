package com.nhnacademy.back.coupon.coupon.domain.entity;

import com.nhnacademy.back.product.category.domain.entity.Category;

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
public class CategoryCoupon {

	@Id
	private Long couponId;

	@MapsId
	@OneToOne(optional = false)
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	public CategoryCoupon(Coupon coupon, Category category) {
		this.coupon = coupon;
		this.category = category;
	}
}
