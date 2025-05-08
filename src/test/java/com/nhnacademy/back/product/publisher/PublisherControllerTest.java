package com.nhnacademy.back.product.publisher;

import static org.mockito.Mockito.*;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.publisher.controller.PublisherController;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.service.PublisherService;

@WebMvcTest(controllers = PublisherController.class)
public class PublisherControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private PublisherService publisherService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Publisher 리스트 조회")
	void get_all_publishers_test() throws Exception {
		Publisher publisherA = new Publisher("Publisher A");
		Publisher publisherB = new Publisher("Publisher B");
		Publisher publisherC = new Publisher("Publisher C");
		List<Publisher> publishers = List.of(publisherA, publisherB, publisherC);

		when(publisherService.getPublishers()).thenReturn(publishers);

		mockMvc.perform(get("/api/admin/publishers"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.publishers").isArray())
			.andExpect(jsonPath("$.publishers[0].publisherName").value("Publisher A"))
			.andExpect(jsonPath("$.publishers[1].publisherName").value("Publisher B"))
			.andExpect(jsonPath("$.publishers[2].publisherName").value("Publisher C"));
	}

	@Test
	@DisplayName("Publisher 저장")
	void create_publisher_test() throws Exception {
		RequestPublisherDTO request = new RequestPublisherDTO("new publisher");

		String jsonRequest = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/admin/publishers")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Publisher 수정")
	void update_publisher_test() throws Exception {
		RequestPublisherDTO registerRequest = new RequestPublisherDTO("update before publisher");
		publisherService.createPublisher(registerRequest);

		RequestPublisherDTO modifyRequest = new RequestPublisherDTO("update after publisher");

		String jsonRequest = objectMapper.writeValueAsString(modifyRequest);

		mockMvc.perform(put("/api/admin/publishers/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}
}
