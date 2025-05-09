package com.nhnacademy.back.product.position;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.contributor.controller.PositionController;
import com.nhnacademy.back.product.contributor.domain.dto.request.RequestPositionDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponsePositionDTO;
import com.nhnacademy.back.product.contributor.service.PositionService;

@WebMvcTest(PositionController.class)
public class PositionControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PositionService positionService;

	@Test
	@DisplayName("position 생성")
	void createPositionTest() throws Exception {
		RequestPositionDTO requestPositionDTO = new RequestPositionDTO("newPosition");
		mockMvc.perform(post("/api/admin/positions")
			.content(objectMapper.writeValueAsString(requestPositionDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}


	@Test
	@DisplayName("position 목록 조회(페이징 처리)")
	void getPositionsTest() throws Exception {
		List<ResponsePositionDTO> mockList = List.of(
			new ResponsePositionDTO(1L,"position1"),
			new ResponsePositionDTO(2L,"position2")
		);

		Page<ResponsePositionDTO> mockPage = new PageImpl<>(mockList);
		Pageable pageable = PageRequest.of(0, 10);

		when(positionService.getPositions(pageable)).thenReturn(mockPage);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/positions")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].positionName").value("position1"));


	}

	@Test
	@DisplayName("positionId로 position 조회")
	void getPositionByIdTest() throws Exception {
		ResponsePositionDTO responsePositionDTO = new ResponsePositionDTO(1L,"position1");
		when(positionService.getPosition(1L)).thenReturn(responsePositionDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/positions/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.positionName").value("position1"));
	}

	@Test
	@DisplayName("position 수정")
	void updatePositionTest() {

		RequestPositionDTO requestPositionDTO = new RequestPositionDTO("newPosition");
		ResponsePositionDTO responseDTO = new ResponsePositionDTO(1L,"newPosition");


		when(positionService.updatePosition(1L, requestPositionDTO)).thenReturn(responseDTO);


		ResponsePositionDTO result = positionService.updatePosition(1L, requestPositionDTO);
		assertEquals("newPosition", result.getPositionName());
		verify(positionService).updatePosition(1L, requestPositionDTO);
	}



}
