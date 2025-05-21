package com.nhnacademy.back.review.controller;

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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.service.ReviewService;

@WebMvcTest(ReviewRestController.class)
class ReviewRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewService reviewService;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	@DisplayName("POST /api/reviews - 리뷰 작성 테스트")
	void createReview() throws Exception {
		// given
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, 1L, "", "상품 좋네요", 4, null);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/reviews")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isCreated());

		verify(reviewService).createReview(any(RequestCreateReviewDTO.class));
	}

	@Test
	@DisplayName("POST /api/reviews/{reviewId} - 리뷰 수정 테스트")
	void updateReview() throws Exception {
		// given
		RequestUpdateReviewDTO request = new RequestUpdateReviewDTO("리뷰 수정", 5, "update.jpg");
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/reviews/{reviewId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isNoContent());

		verify(reviewService).updateReview(eq(1L), any(RequestUpdateReviewDTO.class));
	}

	@Test
	@DisplayName("GET /api/customers/{customerId}/reviews - 고객 리뷰 페이징 목록 조회 테스트")
	void getReviewsByCustomer() throws Exception {
		// given
		Page<ResponseReviewPageDTO> page = new PageImpl<>(List.of(
			new ResponseReviewPageDTO(1L, 1L, 1L, "홍길동", "좋네요", 5, "default.jpg", LocalDateTime.now()),
			new ResponseReviewPageDTO(2L, 2L, 1L, "홍길동", "별로네요", 1, "default.jpg", LocalDateTime.now())
		));

		when(reviewService.getReviewsByCustomer(eq(1L), any(Pageable.class))).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/customers/{customerId}/reviews", 1L)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.size()").value(2))
			.andExpect(jsonPath("$.content[0].reviewContent").value("좋네요"));
	}

	@Test
	@DisplayName("GET /api/products/{productId}/reviews - 상품 리뷰 페이징 목록 조회 테스트")
	void getReviewsByProduct() throws Exception {
		// given
		Page<ResponseReviewPageDTO> page = new PageImpl<>(List.of(
			new ResponseReviewPageDTO(1L, 1L, 1L, "셀린", "좋네요", 5, "default.jpg", LocalDateTime.now()),
			new ResponseReviewPageDTO(2L, 1L, 2L, "홍길동", "별로네요", 1, "default.jpg", LocalDateTime.now())
		));

		when(reviewService.getReviewsByProduct(eq(1L), any(Pageable.class))).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/products/{productId}/reviews", 1L)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.size()").value(2))
			.andExpect(jsonPath("$.content[0].reviewContent").value("좋네요"));
	}

}
