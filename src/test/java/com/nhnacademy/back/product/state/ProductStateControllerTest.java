package com.nhnacademy.back.product.state;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.product.state.controller.ProductStateController;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.service.ProductStateService;

@WebMvcTest(ProductStateController.class)
class ProductStateControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	ProductStateService productStateService;

	@Test
	@DisplayName("GET /api/productState - 전체 조회 성공")
	void getProductStates_success() throws Exception {
		// given
		ProductState state = new ProductState(1L, ProductStateName.SALE);
		given(productStateService.getProductStates()).willReturn(List.of(state));

		// when & then
		mockMvc.perform(get("/api/productState")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productStateId").value(1L))
			.andExpect(jsonPath("$[0].productStateName").value("SALE"));
	}

}