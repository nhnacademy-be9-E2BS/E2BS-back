package com.nhnacademy.back.order.wrapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.order.wrapper.controller.WrapperController;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperMetaDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.service.WrapperService;

@WebMvcTest(controllers = WrapperController.class)
class WrapperControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private WrapperService wrapperService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Wrapper 리스트 조회 - 판매 중")
	void get_wrappers_by_saleable_test() throws Exception {
		// given
		ResponseWrapperDTO responseA = new ResponseWrapperDTO(1L, 700L, "Wrapper A", "a.jpg", true);
		ResponseWrapperDTO responseC = new ResponseWrapperDTO(3L, 900L, "Wrapper C", "c.jpg", true);
		List<ResponseWrapperDTO> dtos = List.of(responseA, responseC);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseWrapperDTO> wrappers = new PageImpl<>(dtos, pageable, dtos.size());

		when(wrapperService.getWrappersBySaleable(true, pageable)).thenReturn(wrappers);

		// when & then
		mockMvc.perform(get("/api/wrappers")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].wrapperName").value("Wrapper A"))
			.andExpect(jsonPath("$.content[0].wrapperSaleable").value(true))
			.andExpect(jsonPath("$.content[1].wrapperName").value("Wrapper C"))
			.andExpect(jsonPath("$.content[1].wrapperSaleable").value(true));
	}

	@Test
	@DisplayName("Wrapper 리스트 조회 - 모두")
	void get_wrappers_test() throws Exception {
		// given
		ResponseWrapperDTO responseA = new ResponseWrapperDTO(1L, 700L, "Wrapper A", "a.jpg", true);
		ResponseWrapperDTO responseB = new ResponseWrapperDTO(2L, 1000L, "Wrapper B", "b.jpg", false);
		List<ResponseWrapperDTO> dtos = List.of(responseA, responseB);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseWrapperDTO> wrappers = new PageImpl<>(dtos, pageable, dtos.size());

		when(wrapperService.getWrappers(pageable)).thenReturn(wrappers);

		// when & then
		mockMvc.perform(get("/api/auth/admin/wrappers")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].wrapperName").value("Wrapper A"))
			.andExpect(jsonPath("$.content[0].wrapperSaleable").value(true))
			.andExpect(jsonPath("$.content[1].wrapperName").value("Wrapper B"))
			.andExpect(jsonPath("$.content[1].wrapperSaleable").value(false));
	}

	@Test
	@DisplayName("Wrapper 저장")
	void create_wrapper_test() throws Exception {
		// given
		MockMultipartFile mockFile = new MockMultipartFile(
			"wrapperImage",
			"a.jpg",
			"image/jpeg",
			"image-content".getBytes()
		);

		RequestRegisterWrapperMetaDTO meta = new RequestRegisterWrapperMetaDTO(1000L, "Wrapper A", true);
		MockMultipartFile metaPart = new MockMultipartFile(
			"requestMeta",
			"",
			"application/json",
			new ObjectMapper().writeValueAsBytes(meta)
		);

		// when & then
		mockMvc.perform(multipart("/api/auth/admin/wrappers")
				.file(metaPart)
				.file(mockFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Wrapper 수정")
	void update_wrapper_test() throws Exception {
		// given
		RequestModifyWrapperDTO request = new RequestModifyWrapperDTO(true);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/auth/admin/wrappers/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}
}
