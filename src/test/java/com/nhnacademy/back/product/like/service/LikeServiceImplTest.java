package com.nhnacademy.back.product.like.service;

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
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;
import com.nhnacademy.back.product.like.domain.entity.Like;
import com.nhnacademy.back.product.like.exception.LikeAlreadyExistsException;
import com.nhnacademy.back.product.like.exception.LikeNotFoundException;
import com.nhnacademy.back.product.like.repository.LikeJpaRepository;
import com.nhnacademy.back.product.like.service.impl.LikeServiceImpl;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private MemberJpaRepository memberRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private LikeJpaRepository likeRepository;

	@Mock
	private ReviewJpaRepository reviewRepository;

	@Mock
	private MinioUtils minioUtils;

	@InjectMocks
	private LikeServiceImpl likeService;

	long productId = 1L;
	long customerId = 1L;
	String memberId = "member1Id";

	Customer customer;
	Member member;
	Product product;

	@BeforeEach
	void setUp() {
		customer = new Customer(customerId, "abc@gmail.com", "pwd12345", "홍길동");
		member = mock(Member.class);

		product = new Product(1L, new ProductState(ProductStateName.SALE), new Publisher("a"),
			"Product A", "content", "description", LocalDate.now(), "isbn",
			10000, 10000, false, 3, List.of(new ProductImage(product, "imageUrl")));
	}

	@Test
	@DisplayName("좋아요 생성")
	void createLike() {
		// given
		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)).thenReturn(false);
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		// when
		likeService.createLike(productId, memberId);

		// then
		verify(likeRepository).save(any(Like.class));
	}

	@Test
	@DisplayName("좋아요 생성 - 실패(LikeAlreadyExistsException)")
	void createLike_Fail_LikeAlreadyExistsException() {
		// given
		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)).thenReturn(true);

		// when & then
		assertThrows(LikeAlreadyExistsException.class, () -> likeService.createLike(productId, memberId));
	}

	@Test
	@DisplayName("좋아요 삭제")
	void deleteLike() {
		// given
		Like like = mock(Like.class);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)).thenReturn(true);
		when(likeRepository.findByCustomer_CustomerIdAndProduct_ProductId(customerId, productId)).thenReturn(
			Optional.of(like));

		// when
		likeService.deleteLike(productId, memberId);

		// then
		verify(likeRepository).delete(like);
	}

	@Test
	@DisplayName("좋아요 삭제 - 실패(LikeNotFoundException)")
	void deleteLike_Fail_LikeNotFoundException() {
		// given
		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)).thenReturn(false);

		// when & then
		assertThrows(LikeNotFoundException.class, () -> likeService.deleteLike(productId, memberId));
	}

	@Test
	@DisplayName("회원이 좋아요한 상품 페이징 목록 조회")
	void getLikedProductsByCustomer() {
		// given
		Like like = new Like(1L, product, customer, LocalDateTime.now());
		Pageable pageable = PageRequest.of(0, 6);
		Page<Product> likeProductPage = new PageImpl<>(List.of(product));

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(likeRepository.findLikedProductsByCustomerId(customerId, pageable)).thenReturn(likeProductPage);
		when(likeRepository.findByCustomer_CustomerIdAndProduct_ProductId(customerId, productId)).thenReturn(
			Optional.of(like));
		when(reviewRepository.totalAvgReviewsByProductId(productId)).thenReturn(4.5);
		when(reviewRepository.countAllByProduct_ProductId(productId)).thenReturn(3);
		when(likeRepository.countAllByProduct_ProductId(productId)).thenReturn(5L);

		// when
		Page<ResponseLikedProductDTO> result = likeService.getLikedProductsByCustomer(memberId, pageable);

		// then
		assertEquals(1, result.getSize());
		ResponseLikedProductDTO dto = result.getContent().getFirst();

		assertEquals(productId, dto.getProductId());
		assertEquals(product.getProductTitle(), dto.getProductTitle());
		assertEquals(product.getProductSalePrice(), dto.getProductSalePrice());
		assertEquals(product.getPublisher().getPublisherName(), dto.getPublisherName());
		assertEquals(5L, dto.getLikeCount());
		assertEquals(4.5, dto.getAvgRating());
		assertEquals(3, dto.getReviewCount());

		verify(memberRepository).getMemberByMemberId(memberId);
		verify(likeRepository).findLikedProductsByCustomerId(customerId, pageable);
		verify(reviewRepository).totalAvgReviewsByProductId(productId);
		verify(reviewRepository).countAllByProduct_ProductId(productId);
		verify(likeRepository).countAllByProduct_ProductId(productId);
		verify(likeRepository).findByCustomer_CustomerIdAndProduct_ProductId(customerId, productId);
	}

	@Test
	@DisplayName("좋아요 개수 조회")
	void getLikeCount() {
		// given
		when(likeRepository.countAllByProduct_ProductId(productId)).thenReturn(7L);

		// when
		long count = likeService.getLikeCount(productId);

		// then
		assertEquals(7L, count);
	}

}
