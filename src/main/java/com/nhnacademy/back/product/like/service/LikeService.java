package com.nhnacademy.back.product.like.service;

public interface LikeService {
	void createLike(long productId, long customerId);
	void deleteLike(long productId, long customerId);
	// Page<ResponseProductReadDTO> getLikeProducts(long customerId, Pageable pageable);
	long getLikeCount(long productId);
}
