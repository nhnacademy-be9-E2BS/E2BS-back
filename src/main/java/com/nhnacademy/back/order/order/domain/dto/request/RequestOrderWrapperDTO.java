package com.nhnacademy.back.order.order.domain.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrderWrapperDTO {
	// 주문서를 저장할 때 사용하는 요청 DTO
	@Valid
	private RequestOrderDTO order;

	@Valid
	@NotEmpty
	private List<RequestOrderDetailDTO> orderDetails;
}
