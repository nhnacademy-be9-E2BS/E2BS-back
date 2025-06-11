package com.nhnacademy.back.product.tag;

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
import com.nhnacademy.back.product.tag.controller.TagController;
import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.service.TagService;

@WebMvcTest(controllers = TagController.class)
class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private TagService tagService;

	@Test
	@DisplayName("Tag 리스트 조회")
	void getAllTagTest() throws Exception {
		//given
		ResponseTagDTO responseA = new ResponseTagDTO(1L, "Tag A");
		ResponseTagDTO responseB = new ResponseTagDTO(2L, "Tag B");
		ResponseTagDTO responseC = new ResponseTagDTO(3L, "Tag C");
		List<ResponseTagDTO> tagDTOList = List.of(responseA, responseB, responseC);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseTagDTO> wrappers = new PageImpl<>(tagDTOList, pageable, tagDTOList.size());

		when(tagService.getTags(pageable)).thenReturn(wrappers);

		//when & then
		mockMvc.perform(get("/api/auth/admin/tags")
				.param("page", "0")
				.param("size", "0")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].tagName").value("Tag A"))
			.andExpect(jsonPath("$.content[1].tagName").value("Tag B"))
			.andExpect(jsonPath("$.content[2].tagName").value("Tag C"));
	}

	@Test
	@DisplayName("tag 저장")
	void createTagTest() throws Exception {
		//given
		RequestTagDTO request = new RequestTagDTO("new tag");
		String jsonRequest = objectMapper.writeValueAsString(request);

		//when & then
		mockMvc.perform(post("/api/auth/admin/tags")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("tag 수정")
	void updateTagTest() throws Exception {
		//given
		RequestTagDTO request = new RequestTagDTO("update tag");
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/auth/admin/tags/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

}
