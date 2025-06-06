package com.nhnacademy.back.order.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.model.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseMemberOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseMemberRecentOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

public interface OrderService {
	ResponseEntity<ResponseOrderResultDTO> createOrder(RequestOrderWrapperDTO requestOrderWrapperDTO);

	ResponseEntity<ResponseOrderResultDTO> createPointOrder(RequestOrderWrapperDTO requestOrderWrapperDTO);

	ResponseEntity<ResponseTossPaymentConfirmDTO> confirmOrder(String orderId, String paymentKey, long amount);

	ResponseEntity<Void> deleteOrder(String orderId);

	ResponseOrderWrapperDTO getOrderByOrderCode(String orderCode);

	Page<ResponseOrderDTO> getOrdersByMemberId(Pageable pageable, String memberId);

	Page<ResponseOrderDTO> getOrdersByCustomerId(Pageable pageable, long customerId);

	ResponseEntity<Void> cancelOrder(String orderCode);

	ResponseEntity<Void> returnOrder(RequestOrderReturnDTO returnDTO);

	long getAllOrders();

	long getTotalSales();

	long getTotalMonthlySales();

	long getTotalDailySales();

	ResponseMemberOrderDTO getMemberOrdersCnt(String memberId);

	List<ResponseMemberRecentOrderDTO> getMemberRecentOrders(String memberId);
}
