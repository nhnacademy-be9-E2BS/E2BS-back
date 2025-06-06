package com.nhnacademy.back.order.order.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderWrapperDTO {
	// 주문서의 상세 정보를 조회할 때 사용하는 응답 DTO
	ResponseOrderDTO order;
	List<ResponseOrderDetailDTO> orderDetails;
}
