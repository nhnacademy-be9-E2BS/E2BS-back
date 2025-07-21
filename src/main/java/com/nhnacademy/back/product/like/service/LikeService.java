package com.nhnacademy.back.product.like.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikeDTO;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;

public interface LikeService {
	void createLike(long productId, String memberId);
	void deleteLike(long productId, String memberId);
	Page<ResponseLikedProductDTO> getLikedProductsByCustomer(String memberId, Pageable pageable);
	long getLikeCount(long productId);

	boolean isLiked(long customerId, long productId);
	ResponseLikeDTO findByCustomerIdAndProductId(long customerId, long productId);
}
