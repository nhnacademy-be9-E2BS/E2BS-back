package com.nhnacademy.back.account.pointhistory;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.account.pointhistory.controller.PointHistoryController;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;

@WebMvcTest(PointHistoryController.class)
class PointHistoryControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	PointHistoryService pointHistoryService;

	@Test
	void getPointList_ReturnsPagedPointHistory() throws Exception {
		String memberId = "user";

		ResponsePointHistoryDTO dto = new ResponsePointHistoryDTO(
			100L,
			"적립",
			LocalDateTime.now()
		);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponsePointHistoryDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

		when(pointHistoryService.getPointHistoryByMemberId(eq(memberId), any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/auth/mypage/{memberId}/pointHistory", memberId)
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].pointAmount").value(100))
			.andExpect(jsonPath("$.content[0].pointReason").value("적립"))
			.andExpect(jsonPath("$.content.length()").value(1));

		verify(pointHistoryService).getPointHistoryByMemberId(eq(memberId), any(Pageable.class));
	}
}
