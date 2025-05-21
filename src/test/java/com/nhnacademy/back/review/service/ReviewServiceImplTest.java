package com.nhnacademy.back.review.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.entity.Review;
import com.nhnacademy.back.review.exception.ReviewNotFoundException;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;
import com.nhnacademy.back.review.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private ReviewJpaRepository reviewRepository;

	@InjectMocks
	private ReviewServiceImpl reviewService;


	Customer customer;
	Product product;

	@BeforeEach
	void setUp() {
		customer = new Customer(1L, "abc@gmail.com", "pwd12345", "홍길동");
		product = new Product(1L, new ProductState(ProductStateName.SALE), new Publisher("a"),
			"Product A", "content", "description", LocalDate.now(), "isbn",
			10000, 10000, false, 3, 0,0, null);
	}


	@Test
	@DisplayName("리뷰 생성 테스트")
	void createReview() {
		// given
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, 1L, "좋네요", 5, "default.jpg");

		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when
		reviewService.createReview(request);

		// then
		verify(reviewRepository, times(1)).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 생성 테스트 - 실패(고객을 찾지 못한 경우)")
	void createReview_Fail_CustomerNotFound() {
		// given
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, 1L, "좋네요", 5, "default.jpg");

		when(customerRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.createReview(request))
			.isInstanceOf(CustomerNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 수정 테스트")
	void updateReview() {
		// given
		long reviewId = 1L;
		RequestUpdateReviewDTO request = new RequestUpdateReviewDTO("내용 수정", 3, "update.jpg");

		Review review = mock(Review.class);
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when
		reviewService.updateReview(reviewId, request);

		// then
		verify(review).changeReview(request);
	}

	@Test
	@DisplayName("리뷰 수정 테스트 - 실패(리뷰를 찾지 못한 경우)")
	void updateReview_Fail_ReviewNotFound() {
		// given
		RequestUpdateReviewDTO request = new RequestUpdateReviewDTO();
		long reviewId = 100L;

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.updateReview(reviewId, request))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	@DisplayName("고객 리뷰 페이징 목록 조회 테스트")
	void getReviewsByCustomer() {
		// given
		long customerId = 1L;
		Pageable pageable = PageRequest.of(0, 5);

		Review review = mock(Review.class);
		when(review.getReviewId()).thenReturn(1L);
		when(review.getReviewContent()).thenReturn("좋네요");
		when(review.getReviewGrade()).thenReturn(5);
		when(review.getReviewImage()).thenReturn("default.jpg");
		when(review.getReviewCreatedAt()).thenReturn(LocalDateTime.now());

		when(review.getCustomer()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);

		Page<Review> reviewPage = new PageImpl<>(List.of(review));
		when(reviewRepository.findAllByCustomer_CustomerId(customerId, pageable)).thenReturn(reviewPage);

		// when
		Page<ResponseReviewPageDTO> result = reviewService.getReviewsByCustomer(customerId, pageable);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals("좋네요", result.getContent().getFirst().getReviewContent());
	}

	@Test
	@DisplayName("상품 리뷰 페이징 목록 조회 테스트")
	void getReviewsByProduct() {
		// given
		long productId = 1L;
		Pageable pageable = PageRequest.of(0, 5);

		Review review = mock(Review.class);
		when(review.getReviewId()).thenReturn(1L);
		when(review.getReviewContent()).thenReturn("좋네요");
		when(review.getReviewGrade()).thenReturn(5);
		when(review.getReviewImage()).thenReturn("default.jpg");
		when(review.getReviewCreatedAt()).thenReturn(LocalDateTime.now());

		when(review.getCustomer()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);

		Page<Review> reviewPage = new PageImpl<>(List.of(review));
		when(reviewRepository.findAllByProduct_ProductId(productId, pageable)).thenReturn(reviewPage);

		// when
		Page<ResponseReviewPageDTO> result = reviewService.getReviewsByProduct(productId, pageable);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals("좋네요", result.getContent().getFirst().getReviewContent());
	}

}
