package com.nhnacademy.back.product.publisher;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.publisher.controller.PublisherController;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.publisher.service.PublisherService;

@WebMvcTest(controllers = PublisherController.class)
class PublisherControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private PublisherService publisherService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Publisher 리스트 조회")
	void get_all_publishers_test() throws Exception {
		// given
		ResponsePublisherDTO responseA = new ResponsePublisherDTO(1L, "Publisher A");
		ResponsePublisherDTO responseB = new ResponsePublisherDTO(2L, "Publisher B");
		ResponsePublisherDTO responseC = new ResponsePublisherDTO(3L, "Publisher C");
		List<ResponsePublisherDTO> dtos = List.of(responseA, responseB, responseC);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponsePublisherDTO> wrappers = new PageImpl<>(dtos, pageable, dtos.size());

		when(publisherService.getPublishers(pageable)).thenReturn(wrappers);

		// when & then
		mockMvc.perform(get("/api/admin/publishers")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].publisherName").value("Publisher A"))
			.andExpect(jsonPath("$.content[1].publisherName").value("Publisher B"))
			.andExpect(jsonPath("$.content[2].publisherName").value("Publisher C"));
	}

	@Test
	@DisplayName("Publisher 저장")
	void create_publisher_test() throws Exception {
		// given
		RequestPublisherDTO request = new RequestPublisherDTO("new publisher");
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/admin/publishers")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Publisher 수정")
	void update_publisher_test() throws Exception {
		// given
		RequestPublisherDTO modifyRequest = new RequestPublisherDTO("update after publisher");
		String jsonRequest = objectMapper.writeValueAsString(modifyRequest);

		// when & then
		mockMvc.perform(put("/api/admin/publishers/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}
}
