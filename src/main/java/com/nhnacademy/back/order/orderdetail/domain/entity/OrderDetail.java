package com.nhnacademy.back.order.orderdetail.domain.entity;

import com.nhnacademy.back.order.order.domain.Order;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.review.domain.entity.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderDetailId;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	private int orderQuantity = 0;

	@Column(nullable = false)
	private long orderDetailPerPrice;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_code")
	private Order order;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_state_id")
	private OrderState orderState;

	@ManyToOne
	@JoinColumn(name = "wrapper_id")
	private Wrapper wrapper;

	@OneToOne
	@JoinColumn(name = "review_id")
	private Review review;

}
