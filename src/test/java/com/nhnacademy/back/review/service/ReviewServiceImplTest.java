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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.order.order.model.entity.OrderDetail;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseUpdateReviewDTO;
import com.nhnacademy.back.review.domain.entity.Review;
import com.nhnacademy.back.review.exception.ReviewNotFoundException;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;
import com.nhnacademy.back.review.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private MemberJpaRepository memberRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private OrderDetailJpaRepository orderDetailRepository;

	@Mock
	private ReviewJpaRepository reviewRepository;

	@Mock
	private ProductSearchService productSearchService;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private MinioUtils minioUtils;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ReviewServiceImpl reviewService;

	Customer customer;
	Member member;
	Product product;

	final long customerId = 1L;

	@BeforeEach
	void setUp() {
		customer = new Customer(customerId, "abc@gmail.com", "pwd12345", "홍길동");
		member = Mockito.mock(Member.class);

		product = new Product(1L, new ProductState(ProductStateName.SALE), new Publisher("a"),
			"Product A", "content", "description", LocalDate.now(), "isbn",
			10000, 10000, false, 3, null);
	}

	@Test
	@DisplayName("리뷰 생성 테스트")
	void createReview() {
		// given
		MockMultipartFile mockFile = new MockMultipartFile("reviewImage", "test-image.jpg", "image/jpeg",
			"dummy image content".getBytes());
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, customerId, "", "좋네요", 5, mockFile);

		when(memberRepository.getMemberByMemberId(anyString())).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(customerId)).thenReturn(Optional.of(product));
		when(reviewRepository.existsReviewedOrderDetailsByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(true);
		doNothing().when(minioUtils).uploadObject(anyString(), anyString(), any(MultipartFile.class));
		when(orderDetailRepository.findByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(
			Optional.of(mock(OrderDetail.class)));

		// when
		reviewService.createReview(request);

		// then
		verify(reviewRepository, times(1)).save(any(Review.class));
		verify(productSearchService, times(1)).updateProductDocumentReview(anyLong(), anyInt());
	}

	@Test
	@DisplayName("리뷰 생성 테스트 - 실패(고객을 찾지 못한 경우)")
	void createReview_Fail_CustomerNotFound() {
		// given
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, customerId, "", "좋네요", 5, null);

		when(memberRepository.getMemberByMemberId(anyString())).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.createReview(request))
			.isInstanceOf(CustomerNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 수정 테스트")
	void updateReview() {
		// given
		long reviewId = 1L;
		MockMultipartFile mockUpdateFile = new MockMultipartFile("reviewImage", "update-image.jpg", "image/jpeg",
			"dummy image content".getBytes());
		RequestUpdateReviewDTO request = new RequestUpdateReviewDTO("수정된 내용", mockUpdateFile);

		Review review = mock(Review.class);
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		doNothing().when(minioUtils).uploadObject(anyString(), anyString(), any(MultipartFile.class));
		when(minioUtils.getPresignedUrl(any(), any())).thenReturn("storageUrl");

		// when
		ResponseUpdateReviewDTO result = reviewService.updateReview(reviewId, request);

		// then
		assertEquals("수정된 내용", result.getReviewContent());
		assertEquals("storageUrl", result.getReviewImageUrl());
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
	@DisplayName("상품 리뷰 페이징 목록 조회 테스트")
	void getReviewsByProduct() {
		// given
		Pageable pageable = PageRequest.of(0, 5);

		Review review = new Review(1L, product, customer, "좋네요", 5, "default.jpg", LocalDateTime.now());

		Page<Review> reviewPage = new PageImpl<>(List.of(review));
		when(reviewRepository.findAllByProduct_ProductId(product.getProductId(), pageable)).thenReturn(reviewPage);

		// when
		Page<ResponseReviewPageDTO> result = reviewService.getReviewsByProduct(product.getProductId(), pageable);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals("좋네요", result.getContent().getFirst().getReviewContent());
	}

	@Test
	@DisplayName("리뷰 정보 조회")
	void getReviewInfo() {
		// given
		when(reviewRepository.totalAvgReviewsByProductId(1L)).thenReturn(4.24);
		when(reviewRepository.countAllByProduct_ProductId(1L)).thenReturn(10);
		when(reviewRepository.countAllByProduct_ProductIdAndReviewGrade(anyLong(), anyInt())).thenReturn(2);

		// when
		ResponseReviewInfoDTO result = reviewService.getReviewInfo(1L);

		// then
		assertEquals(4.2, result.getTotalGradeAvg());
		assertEquals(10, result.getTotalCount());
		assertEquals(5, result.getStarCounts().size());
	}
}
