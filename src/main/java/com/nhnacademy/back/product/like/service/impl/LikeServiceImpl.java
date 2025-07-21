package com.nhnacademy.back.product.like.service.impl;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikeDTO;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;
import com.nhnacademy.back.product.like.domain.entity.Like;
import com.nhnacademy.back.product.like.exception.LikeAlreadyExistsException;
import com.nhnacademy.back.product.like.exception.LikeNotFoundException;
import com.nhnacademy.back.product.like.repository.LikeJpaRepository;
import com.nhnacademy.back.product.like.service.LikeService;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
	private static final String NOT_FOUND_MEMBER = "아이디에 해당하는 회원을 찾지 못했습니다.";

	private final CustomerJpaRepository customerRepository;
	private final MemberJpaRepository memberRepository;
	private final ProductJpaRepository productRepository;
	private final LikeJpaRepository likeRepository;
	private final ReviewJpaRepository reviewRepository;

	private final MinioUtils minioUtils;
	private static final String PRODUCT_BUCKET = "e2bs-products-image";


	/**
	 * 좋아요 생성 메소드
	 */
	@Transactional
	@Override
	public void createLike(long productId, String memberId) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}

		long customerId = findMember.getCustomerId();
		if (likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)) {
			throw new LikeAlreadyExistsException();
		}

		Product findProduct = productRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		Customer findCustomer = customerRepository.findById(customerId)
			.orElseThrow(CustomerNotFoundException::new);

		Like likeEntity = Like.createLikeEntity(findProduct, findCustomer);
		likeRepository.save(likeEntity);
	}

	/**
	 * 좋아요 삭제 메소드
	 */
	@Transactional
	@Override
	public void deleteLike(long productId, String memberId) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}

		long customerId = findMember.getCustomerId();
		if (!likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)) {
			throw new LikeNotFoundException();
		}

		Like findLike = likeRepository.findByCustomer_CustomerIdAndProduct_ProductId(customerId, productId)
			.orElseThrow(LikeNotFoundException::new);

		likeRepository.delete(findLike);
	}

	/**
	 * 회원이 좋아요한 상품 페이징 목록 조회 메소드
	 */
	@Override
	public Page<ResponseLikedProductDTO> getLikedProductsByCustomer(String memberId, Pageable pageable) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}

		long customerId = findMember.getCustomerId();

		Page<ResponseLikedProductDTO> dtoPage = likeRepository.findLikedProductsByCustomerId(customerId, pageable);

		return dtoPage.map(dto -> {
			Product findProduct = productRepository.findById(dto.getProductId())
				.orElseThrow(ProductNotFoundException::new);

			String thumbnailPath = "";
			if (dto.getProductThumbnail().isEmpty()) {
				String imgPath = findProduct.getProductImage().getFirst().getProductImagePath();
				if (imgPath.startsWith("http")) {
					thumbnailPath = imgPath;
				} else {
					thumbnailPath = minioUtils.getPresignedUrl(PRODUCT_BUCKET, imgPath);
				}
			}
			dto.setProductThumbnail(thumbnailPath);
			return dto;
		});
	}

	/**
	 * 상품 좋아요 개수 조회 메소드
	 */
	@Override
	public long getLikeCount(long productId) {
		return likeRepository.countAllByProduct_ProductId(productId);
	}

	/**
	 * customerId와 productId 인덱스 설정 테스트
	 */
	@Override
	public boolean isLiked(long customerId, long productId) {
		return likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(customerId, productId);
	}

	/**
	 * 인덱스 설정 테스트
	 */
	@Override
	public ResponseLikeDTO findByCustomerIdAndProductId(long customerId, long productId) {
		Like findLike = likeRepository.findByCustomer_CustomerIdAndProduct_ProductId(customerId, productId)
			.orElseThrow(LikeNotFoundException::new);

		return new ResponseLikeDTO(findLike.getLikeId(), findLike.getCustomer().getCustomerId(), findLike.getProduct().getProductId(), findLike.getLikeCreatedAt());
	}

}
