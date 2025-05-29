package com.nhnacademy.back.product.like.controller;

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
import com.nhnacademy.back.product.like.domain.dto.request.RequestCreateLikeDTO;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;
import com.nhnacademy.back.product.like.service.LikeService;

@WebMvcTest(LikeRestController.class)
class LikeRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private LikeService likeService;


	@Test
	@DisplayName("POST /api/products/{productId}/likes - 좋아요 생성")
	void createLike() throws Exception {
		// given
		RequestCreateLikeDTO requestDto = new RequestCreateLikeDTO();
		requestDto.setMemberId("member1Id");
		String jsonRequest = objectMapper.writeValueAsString(requestDto);

		// when & then
		mockMvc.perform(post("/api/products/{productId}/likes", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk());

		verify(likeService).createLike(1L, "member1Id");
	}

	@Test
	@DisplayName("POST /api/products/{productId}/likes - 좋아요 생성 실패(DTO 검증 실패)")
	void createLike_Fail() throws Exception {
		// given
		RequestCreateLikeDTO requestDto = new RequestCreateLikeDTO();
		String jsonRequest = objectMapper.writeValueAsString(requestDto);

		// when & then
		mockMvc.perform(post("/api/products/1/likes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("DELETE /api/products/{productId}/likes - 좋아요 삭제")
	void deleteLike() throws Exception {
		// when & then
		mockMvc.perform(delete("/api/products/{productId}/likes", 1L)
				.param("memberId", "member1Id"))
			.andExpect(status().isOk());

		verify(likeService).deleteLike(1L, "member1Id");
	}

	@Test
	@DisplayName("GET /api/products/likes - 회원이 좋아요한 상품 목록 페이징 조회")
	void getLikedProductsByCustomer() throws Exception {
		// given
		Page<ResponseLikedProductDTO> mockPage = new PageImpl<>(List.of(), PageRequest.of(0, 6), 0);
		when(likeService.getLikedProductsByCustomer(eq("member1"), any(Pageable.class))).thenReturn(mockPage);

		// when & then
		mockMvc.perform(get("/api/products/likes")
				.param("memberId", "member1")
				.param("page", "0")
				.param("size", "6"))
			.andExpect(status().isOk());

		verify(likeService).getLikedProductsByCustomer(eq("member1"), any(Pageable.class));
	}

	@Test
	@DisplayName("GET /api/products/{productId}/likes/counts - 상품 좋아요 개수 조회")
	void getLikeCounts() throws Exception {
		// given
		when(likeService.getLikeCount(1L)).thenReturn(5L);

		// when & then
		mockMvc.perform(get("/api/products/{productId}/likes/counts", 1L))
			.andExpect(status().isOk())
			.andExpect(content().string("5"));

		verify(likeService).getLikeCount(1L);
	}

}
