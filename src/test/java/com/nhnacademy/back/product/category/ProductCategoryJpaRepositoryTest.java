package com.nhnacademy.back.product.category;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@DataJpaTest
@ActiveProfiles("test")
class ProductCategoryJpaRepositoryTest {
	@Autowired
	private ProductCategoryJpaRepository productCategoryJpaRepository;

	@Autowired
	private TestEntityManager em;

	private Product product1;
	private Product product2;

	@BeforeEach
	void setup() {
		ProductState productState = em.persist(new ProductState(ProductStateName.SALE));
		Publisher publisher = em.persist(new Publisher("publisher"));

		product1 = em.persist(Product.builder()
			.productState(productState)
			.publisher(publisher)
			.productTitle("Product1")
			.productContent("Product1 content")
			.productDescription("Product1 description")
			.productPublishedAt(LocalDate.now())
			.productIsbn("978-89-12345-01-1")
			.productRegularPrice(10000)
			.productSalePrice(8000)
			.productPackageable(true)
			.productStock(100)
			.productImage(new ArrayList<>())
			.build());
		product2 = em.persist(Product.builder()
			.productState(productState)
			.publisher(publisher)
			.productTitle("Product2")
			.productContent("Product2 content")
			.productDescription("Product2 description")
			.productPublishedAt(LocalDate.now())
			.productIsbn("978-89-12345-01-2")
			.productRegularPrice(5000)
			.productSalePrice(4000)
			.productPackageable(true)
			.productStock(100)
			.productImage(new ArrayList<>())
			.build());

		Category category1 = em.persist(new Category("Category1", null));
		Category category2 = em.persist(new Category("Category2", category1));

		em.persist(new ProductCategory(product1, category1));
		em.persist(new ProductCategory(product1, category2));
		em.persist(new ProductCategory(product2, category1));
		em.flush();
	}

	@Test
	@DisplayName("findByProduct_ProductId 메소드 테스트")
	void findByProduct_ProductId_test() {
		List<ProductCategory> result = productCategoryJpaRepository.findByProduct_ProductId(product1.getProductId());
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("findFlatCategoryData 메소드 테스트")
	void findFlatCategoryData_test() {
		List<Long> productIds = List.of(product1.getProductId(), product2.getProductId());
		List<ProductCategoryFlatDTO> result = productCategoryJpaRepository.findFlatCategoryData(productIds);
		assertThat(result).hasSize(3);
	}

	@Test
	@DisplayName("deleteAllByProductId 메소드 테스트")
	void deleteAllByProductId_test() {
		productCategoryJpaRepository.deleteAllByProductId(product1.getProductId());
		em.flush();
		List<ProductCategory> remaining = productCategoryJpaRepository.findByProduct_ProductId(product1.getProductId());
		assertThat(remaining).isEmpty();
	}

	@Test
	@DisplayName("findCategoryDTOsByProductId 메소드 테스트")
	void findCategoryDTOsByProductId_test() {
		List<ResponseCategoryDTO> dtos = productCategoryJpaRepository.findCategoryDTOsByProductId(
			product1.getProductId());
		assertThat(dtos).hasSize(2);
		assertThat(dtos.get(0).getCategoryId()).isNotNull();
		assertThat(dtos.get(0).getCategoryName()).isNotNull();
	}

	@Test
	@DisplayName("findAllWithCategoriesByProductIds 메소드 테스트")
	void findAllWithCategoriesByProductIds_test() {
		List<ProductCategory> result = productCategoryJpaRepository.findAllWithCategoriesByProductIds(
			List.of(product1.getProductId(), product2.getProductId()));
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getCategory()).isNotNull();
	}

	@Test
	@DisplayName("findCategoriesGroupedByProductIds 메소드 테스트")
	void findCategoriesGroupedByProductIds_test() {
		Map<Long, List<Category>> grouped = productCategoryJpaRepository.findCategoriesGroupedByProductIds(
			List.of(product1.getProductId(), product2.getProductId()));
		assertThat(grouped.get(product1.getProductId())).hasSize(2);
		assertThat(grouped.get(product2.getProductId())).hasSize(1);
	}
}
