package com.nhnacademy.back.product.like.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.product.like.domain.entity.Like;
import com.nhnacademy.back.product.like.exception.LikeAlreadyExistsException;
import com.nhnacademy.back.product.like.exception.LikeNotFoundException;
import com.nhnacademy.back.product.like.repository.LikeJpaRepository;
import com.nhnacademy.back.product.like.service.LikeService;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

	private final CustomerJpaRepository customerRepository;
	private final ProductJpaRepository productRepository;
	private final LikeJpaRepository likeRepository;


	@Transactional
	@Override
	public void createLike(long productId, long customerId) {
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
	public void deleteLike(long productId, long customerId) {
		if (!likeRepository.existsByProduct_ProductIdAndCustomer_CustomerId(productId, customerId)) {
			throw new LikeNotFoundException();
		}

		Like findLike = likeRepository.findByProduct_ProductIdAndCustomer_CustomerId(productId, customerId).orElseThrow(LikeNotFoundException::new);
		likeRepository.delete(findLike);
	}

	// @Override
	// public Page<ResponseLikedProductDTO> getLikeProducts(long customerId, Pageable pageable) {
	// 	Page<Product> likedProductsByCustomerId = likeRepository.findLikedProductsByCustomerId(customerId, pageable);
	//
	// 	return likedProductsByCustomerId.map(product -> {
	//
	// 		return new ResponseLikedProductDTO(
	// 			product.getProductId(),
	//
	// 		);
	// 	});
	// }

	@Override
	public long getLikeCount(long productId) {
		return likeRepository.countAllByProduct_ProductId(productId);
	}

}
