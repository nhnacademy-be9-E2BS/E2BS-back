package com.nhnacademy.back.product.contributor.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;

public interface ProductContributorJpaRepository extends JpaRepository<ProductContributor, Long> {

	void deleteByProduct_ProductId(long productId);

	@Query(
		"select new com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO(pc.contributor.contributorId, pc.contributor.contributorName,"
			+ "pc.contributor.position.positionId, pc.contributor.position.positionName)"
			+ "from ProductContributor pc where pc.product.productId = :productId")
	List<ResponseContributorDTO> findContributorDTOsByProductId(@Param("productId") Long productId);

	@Query("SELECT pc FROM ProductContributor pc JOIN FETCH pc.contributor c JOIN FETCH c.position WHERE pc.product.productId IN :productIds")
	List<ProductContributor> findAllWithContributorsByProductIds(@Param("productIds") List<Long> productIds);

	default Map<Long, List<Contributor>> findContributorsGroupedByProductIds(List<Long> productIds) {
		return findAllWithContributorsByProductIds(productIds).stream()
			.collect(Collectors.groupingBy(
				pc -> pc.getProduct().getProductId(),
				Collectors.mapping(ProductContributor::getContributor, Collectors.toList())
			));
	}
}
