package com.nhnacademy.back.order.orderreturn.domain.entity;

import com.nhnacademy.back.order.order.domain.entity.OrderDetail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class OrderReturn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderReturnId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String orderReturnReason;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReturnCategory returnCategory;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_detail_id")
	private OrderDetail orderDetail;

}
