package com.nhnacademy.back.order.orderreturn.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.order.order.model.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.model.entity.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class OrderReturn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderReturnId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String orderReturnReason;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReturnCategory returnCategory;

	@Column(nullable = false)
	private LocalDateTime orderReturnCreatedAt;

	@Column(nullable = false)
	private long orderReturnAmount;

	@OneToOne(optional = false)
	@JoinColumn(name = "order_code")
	private Order order;

	public OrderReturn(RequestOrderReturnDTO returnDTO, Order order, long orderReturnAmount) {
		this.orderReturnReason = returnDTO.getOrderReturnReason();
		this.returnCategory = ReturnCategory.valueOf(returnDTO.getReturnCategory());
		this.orderReturnCreatedAt = LocalDateTime.now();
		this.orderReturnAmount = orderReturnAmount;
		this.order = order;
	}
}
