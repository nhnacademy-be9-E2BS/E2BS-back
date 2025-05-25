package com.nhnacademy.back.review.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewMetaDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseUpdateReviewDTO;
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
		RequestCreateReviewMetaDTO requestMeta = new RequestCreateReviewMetaDTO(1L, 1L, "", "상품 좋네요", 4);
		String jsonRequestMeta = objectMapper.writeValueAsString(requestMeta);

		MockMultipartFile requestMetaPart = new MockMultipartFile(
			"requestMeta",
			"requestMeta",
			"application/json",
			jsonRequestMeta.getBytes(StandardCharsets.UTF_8)
		);

		MockMultipartFile reviewImagePart = new MockMultipartFile(
			"reviewImage",
			"image.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"image data".getBytes()
		);

		// when & then
		// MockMvcRequestBuilders.multipart()는 기본이 POST 라서 .with() 필요가 없다.
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/reviews")
				.file(requestMetaPart)
				.file(reviewImagePart))
			.andExpect(status().isCreated());

		verify(reviewService).createReview(any(RequestCreateReviewDTO.class));
	}

	@Test
	@DisplayName("POST /api/reviews/{reviewId} - 리뷰 수정 테스트")
	void updateReview() throws Exception {
		// given
		MockMultipartFile contentPart = new MockMultipartFile(
			"reviewContent",
			"reviewContent",
			"text/plain",
			"리뷰 수정 내용".getBytes()
		);

		MockMultipartFile reviewImagePart = new MockMultipartFile(
			"reviewImage",
			"updateImage.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"image data".getBytes()
		);

		ResponseUpdateReviewDTO mockResponse = new ResponseUpdateReviewDTO();
		when(reviewService.updateReview(any(Long.class), any(RequestUpdateReviewDTO.class))).thenReturn(mockResponse);

		// when & then
		// MockMvcRequestBuilders.multipart() 기본이 POST 라서 .with()로 PUT 설정 필요
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/reviews/{reviewId}", 1L)
				.file(contentPart)
				.file(reviewImagePart)
				.with(req -> {
					req.setMethod("PUT");
					return req;
				}))
			.andExpect(status().isOk());

		verify(reviewService).updateReview(eq(1L), any(RequestUpdateReviewDTO.class));
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

	@Test
	@DisplayName("GET /api/products/{productId}/reviews/info - 리뷰 정보 조회 테스트")
	void getReviewInfo() throws Exception {
		// given
		when(reviewService.getReviewInfo(1L)).thenReturn(new ResponseReviewInfoDTO());

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{productId}/reviews/info", 1L))
			.andExpect(status().isOk());
	}

}
