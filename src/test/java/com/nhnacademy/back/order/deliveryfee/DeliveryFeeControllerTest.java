package com.nhnacademy.back.order.deliveryfee;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.order.deliveryfee.controller.DeliveryFeeController;
import com.nhnacademy.back.order.deliveryfee.domain.dto.request.RequestDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.dto.response.ResponseDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.service.DeliveryFeeService;

@WebMvcTest(controllers = DeliveryFeeController.class)
@ActiveProfiles("dev")
class DeliveryFeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DeliveryFeeService deliveryFeeService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET /api/admin/deliveryFee - 관리자 배송비 정책 목록 조회")
	void testGetDeliveryFee() throws Exception {
		ResponseDeliveryFeeDTO dto = new ResponseDeliveryFeeDTO(1L, 2500, 50000, LocalDateTime.now());
		Page<ResponseDeliveryFeeDTO> page = new PageImpl<>(List.of(dto));
		when(deliveryFeeService.getDeliveryFees(any())).thenReturn(page);

		mockMvc.perform(get("/api/auth/admin/deliveryFee"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST /api/admin/deliveryFee - 배송비 정책 생성")
	void testCreateDeliveryFee() throws Exception {
		RequestDeliveryFeeDTO request = new RequestDeliveryFeeDTO(2500, 50000);

		mockMvc.perform(post("/api/auth/admin/deliveryFee")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		verify(deliveryFeeService).createDeliveryFee(any(RequestDeliveryFeeDTO.class));
	}

	@Test
	@DisplayName("POST /api/admin/deliveryFee - 검증 실패 시 예외 발생")
	void testCreateDeliveryFee_validationFail() throws Exception {
		RequestDeliveryFeeDTO request = new RequestDeliveryFeeDTO(-2500, 50000);

		mockMvc.perform(post("/api/auth/admin/deliveryFee")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());

		verify(deliveryFeeService, times(0)).createDeliveryFee(any(RequestDeliveryFeeDTO.class));
	}

	@Test
	@DisplayName("GET /api/deliveryFee - 현재 적용 중인 배송비 정책 조회")
	void testGetCurrentDeliveryFee() throws Exception {
		ResponseDeliveryFeeDTO dto = new ResponseDeliveryFeeDTO(1L, 2500, 50000, LocalDateTime.now());
		when(deliveryFeeService.getCurrentDeliveryFee()).thenReturn(dto);

		mockMvc.perform(get("/api/deliveryFee"))
			.andExpect(status().isOk());
	}
}
