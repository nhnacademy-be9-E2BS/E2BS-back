package com.nhnacademy.back.order.order.model.dto.response;

import com.nhnacademy.back.order.order.model.entity.OrderDetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDetailDTO {
	private long orderDetailId;
	private int orderQuantity;
	private long orderDetailPerPrice;

	private Long productId;
	private String productName;

	private Long wrapperId;
	private String wrapperName;
	private Long wrapperPrice;

	private Long reviewId;

	public static ResponseOrderDetailDTO fromEntity(OrderDetail orderDetail) {
		return new ResponseOrderDetailDTO(
			orderDetail.getOrderDetailId(),
			orderDetail.getOrderQuantity(),
			orderDetail.getOrderDetailPerPrice(),

			orderDetail.getProduct().getProductId(),
			orderDetail.getProduct().getProductTitle(),

			orderDetail.getWrapper() != null ? orderDetail.getWrapper().getWrapperId() : null,
			orderDetail.getWrapper() != null ? orderDetail.getWrapper().getWrapperName() : null,
			orderDetail.getWrapper() != null ? orderDetail.getWrapper().getWrapperPrice() : null,

			orderDetail.getReview() != null ? orderDetail.getReview().getReviewId() : null
		);
	}
}
