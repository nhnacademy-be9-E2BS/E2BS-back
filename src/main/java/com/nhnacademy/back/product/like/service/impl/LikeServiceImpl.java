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

	private final CustomerJpaRepository customerRepository;
	private final MemberJpaRepository memberRepository;
	private final ProductJpaRepository productRepository;
	private final LikeJpaRepository likeRepository;
	private final ReviewJpaRepository reviewRepository;


	@Transactional
	@Override
	public void createLike(long productId, String memberId) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException("Member not found");
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

	@Transactional
	@Override
	public void deleteLike(long productId, String memberId) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException("Member not found");
		}

		long customerId = findMember.getCustomerId();
		if (!likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)) {
			throw new LikeNotFoundException();
		}

		Like findLike = likeRepository.findByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)
			.orElseThrow(LikeNotFoundException::new);

		likeRepository.delete(findLike);
	}

	@Override
	public Page<ResponseLikedProductDTO> getLikedProductsByCustomer(String memberId, Pageable pageable) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException("Member not found");
		}

		long customerId = findMember.getCustomerId();

		Page<Product> likedProductsByCustomerId = likeRepository.findLikedProductsByCustomerId(customerId, pageable);

		return likedProductsByCustomerId.map(product -> {
			long likeCount = getLikeCount(product.getProductId());
			double reviewAvg = reviewRepository.totalAvgReviewsByProductId(product.getProductId());
			Integer reviewCount = reviewRepository.countAllByProduct_ProductId(product.getProductId());
			Like findLike = likeRepository.findByCustomer_CustomerIdAndProduct_ProductId(customerId, product.getProductId())
				.orElseThrow(LikeNotFoundException::new);

			return new ResponseLikedProductDTO(
				product.getProductId(),
				product.getProductTitle(),
				product.getProductSalePrice(),
				product.getPublisher().getPublisherName(),
				product.getProductImage().getFirst().getProductImagePath(),
				likeCount,
				reviewAvg,
				reviewCount,
				findLike.getLikeCreatedAt()
			);
		});
	}

	@Override
	public long getLikeCount(long productId) {
		return likeRepository.countAllByProduct_ProductId(productId);
	}

}
