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
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.order.order.exception.OrderDetailNotFoundException;
import com.nhnacademy.back.order.order.model.entity.OrderDetail;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseMemberReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewDTO;
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
		member = Member.builder()
			.customer(customer)
			.customerId(customerId)
			.memberId("memberId1")
			.build();

		product = new Product(1L, new ProductState(ProductStateName.SALE), new Publisher("a"),
			"Product A", "content", "description", LocalDate.now(), "isbn",
			10000, 10000, false, 3, null);
	}

	@Test
	@DisplayName("회원 리뷰 생성 테스트")
	void createReview_Member() {
		// given
		OrderDetail orderDetail = mock(OrderDetail.class);

		MultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "data".getBytes());
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(product.getProductId(), null, member.getMemberId(), "내용", 4, imageFile);

		when(memberRepository.getMemberByMemberId(member.getMemberId())).thenReturn(member);
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
		when(reviewRepository.existsReviewedOrderDetailsByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(true);
		when(orderDetailRepository.findByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(List.of(orderDetail));
		doNothing().when(minioUtils).uploadObject(any(), any(), any());
		when(reviewRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		// when
		reviewService.createReview(request);

		// then
		verify(productSearchService).updateProductDocumentReview(product.getProductId(), request.getReviewGrade());
	}

	@Test
	@DisplayName("회원 리뷰 생성 테스트 - 실패(회원 ID로 회원을 찾지 못한 경우)")
	void createReview_Fail_MemberNotFoundException() {
		// given
		when(memberRepository.getMemberByMemberId("unknown")).thenReturn(null);

		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, null, "unknown", "내용", 5, null);

		// when & then
		assertThrows(NotFoundMemberException.class, () -> reviewService.createReview(request));
	}
	
	@Test
	@DisplayName("비회원 리뷰 생성 테스트")
	void createReview_Customer() {
		// given
		OrderDetail orderDetail = mock(OrderDetail.class);

		MultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "data".getBytes());
		RequestCreateReviewDTO request = new RequestCreateReviewDTO(product.getProductId(), customerId, null, "내용", 4, imageFile);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
		when(reviewRepository.existsReviewedOrderDetailsByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(true);
		when(orderDetailRepository.findByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(List.of(orderDetail));
		when(reviewRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		// when
		reviewService.createReview(request);

		// then
		verify(eventPublisher, never()).publishEvent(any()); // 비회원은 이벤트 없음
		verify(productSearchService).updateProductDocumentReview(product.getProductId(), 4);
	}

	@Test
	@DisplayName("비회원 리뷰 생성 테스트 - 실패(고객을 찾지 못한 경우)")
	void createReview_Fail_CustomerNotFoundException() {
		// given
		when(customerRepository.findById(999L)).thenReturn(Optional.empty());

		RequestCreateReviewDTO request = new RequestCreateReviewDTO(1L, 999L, null, "내용", 5, null);

		// when & then
		assertThrows(CustomerNotFoundException.class, () -> reviewService.createReview(request));
	}

	@Test
	@DisplayName("리뷰 생성 테스트 - 실패(상품이 존재하지 않을 경우)")
	void createReview_fail_productNotFound() {
		// given
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(100L)).thenReturn(Optional.empty());

		RequestCreateReviewDTO request = new RequestCreateReviewDTO(100L, customerId, null, "내용", 5, null);

		// when & then
		assertThrows(ProductNotFoundException.class, () -> reviewService.createReview(request));
	}

	@Test
	@DisplayName("리뷰 생성 테스트 - 실패(작성 가능한 주문 상세가 없는 경우)")
	void createReview_fail_orderDetailNotFound() {
		// given
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
		when(reviewRepository.existsReviewedOrderDetailsByCustomerIdAndProductId(customerId, product.getProductId())).thenReturn(false);

		RequestCreateReviewDTO request = new RequestCreateReviewDTO(product.getProductId(), customerId, null, "내용", 5, null);

		// when & then
		assertThrows(OrderDetailNotFoundException.class, () -> reviewService.createReview(request));
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
	@DisplayName("리뷰 정보 조회 테스트")
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

	@Test
	@DisplayName("회원 리뷰 목록 조회 테스트")
	void getReviewsByMember() {
		// given
		Review review = Review.builder()
			.reviewId(1L)
			.reviewContent("좋아요")
			.reviewGrade(5)
			.reviewImage("review.jpg")
			.product(product)
			.customer(Customer.builder().customerId(customerId).build())
			.reviewCreatedAt(LocalDateTime.now())
			.build();

		Pageable pageable = PageRequest.of(0, 5);
		Page<Review> reviewPage = new PageImpl<>(List.of(review));

		when(memberRepository.getMemberByMemberId(member.getMemberId())).thenReturn(member);
		when(reviewRepository.findAllByCustomer_CustomerId(customerId, pageable)).thenReturn(reviewPage);
		when(minioUtils.getPresignedUrl(any(), any())).thenReturn("http://mocked.url");

		// when
		Page<ResponseMemberReviewDTO> result = reviewService.getReviewsByMember(member.getMemberId(), pageable);

		// then
		assertEquals(1, result.getTotalElements());
		verify(minioUtils, times(1)).getPresignedUrl(any(), any());
	}

	@Test
	@DisplayName("회원 리뷰 목록 조회 테스트 - 실패(존재하지 않는 회원)")
	void getReviewsByMember_Fail_NotFoundException() {
		// given
		when(memberRepository.getMemberByMemberId("wrongId")).thenReturn(null);

		// when & then
		assertThrows(NotFoundMemberException.class, () ->
			reviewService.getReviewsByMember("wrongId", Pageable.unpaged())
		);
	}

	@Test
	@DisplayName("리뷰 존재 여부 확인 테스트")
	void existsReviewedOrderCode() {
		// given
		when(reviewRepository.existsReviewedOrderCode("ORD123")).thenReturn(true);

		// when
		boolean result = reviewService.existsReviewedOrderCode("ORD123");

		// then
		assertTrue(result);
	}

	@Test
	@DisplayName("주문 상세 ID로 리뷰 조회 테스트")
	void findByOrderDetailId() {
		// given
		Review review = Review.builder()
			.reviewId(1L)
			.reviewContent("좋음")
			.reviewGrade(4)
			.reviewImage("img.jpg")
			.product(Product.builder().productId(1L).build())
			.customer(Customer.builder().customerId(1L).build())
			.build();

		when(reviewRepository.findByOrderDetailId(100L)).thenReturn(Optional.of(review));
		when(minioUtils.getPresignedUrl(any(), any())).thenReturn("http://signed.url");

		// when
		ResponseReviewDTO result = reviewService.findByOrderDetailId(100L);

		// then
		assertEquals(1L, result.getReviewId());
		assertEquals("http://signed.url", result.getReviewImage());
	}

	@Test
	@DisplayName("주문 상세 ID로 리뷰 조회 테스트 - 실패(리뷰 없음)")
	void findByOrderDetailId_Fail_NotFoundException() {
		// given
		when(reviewRepository.findByOrderDetailId(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThrows(ReviewNotFoundException.class,
			() -> reviewService.findByOrderDetailId(999L));
	}
	
}
