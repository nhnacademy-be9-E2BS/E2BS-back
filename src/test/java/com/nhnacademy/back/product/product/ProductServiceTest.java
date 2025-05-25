package com.nhnacademy.back.product.product;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;
import com.nhnacademy.back.product.contributor.exception.ContributorNotFoundException;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.product.kim.service.impl.ProductServiceImpl;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.exception.ProductStateNotFoundException;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
import com.nhnacademy.back.product.tag.domain.entity.ProductTag;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;
import com.nhnacademy.back.product.tag.repository.ProductTagJpaRepository;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	@InjectMocks
	private ProductServiceImpl productService;
	@Mock
	private ProductJpaRepository productJpaRepository;
	@Mock
	private ProductImageJpaRepository productImageJpaRepository;
	@Mock
	private PublisherJpaRepository publisherJpaRepository;
	@Mock
	private ProductStateJpaRepository productStateJpaRepository;
	@Mock
	private ProductCategoryJpaRepository productCategoryJpaRepository;
	@Mock
	private ContributorJpaRepository contributorJpaRepository;
	@Mock
	private ProductContributorJpaRepository productContributorJpaRepository;
	@Mock
	private TagJpaRepository tagJpaRepository;
	@Mock
	private ProductTagJpaRepository productTagJpaRepository;

	@Test
	@DisplayName("create product - success")
	void create_product_success_test() {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("Publisher");
		Tag tag = new Tag("tag A");
		Position position = new Position("writer");
		Contributor contributor = new Contributor("conA", position);
		when(productJpaRepository.existsByProductIsbn(any())).thenReturn(false);
		when(productStateJpaRepository.findById(anyLong())).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.of(publisher));
		when(tagJpaRepository.findById(anyLong())).thenReturn(Optional.of(tag));
		when(contributorJpaRepository.findById(anyLong())).thenReturn(Optional.of(contributor));

		// when
		productService.createProduct(request);

		// then
		verify(productJpaRepository, times(1)).save(any(Product.class));
		verify(productImageJpaRepository, times(2)).save(any());
		verify(productTagJpaRepository, times(1)).save(any(ProductTag.class));
		verify(productContributorJpaRepository, times(1)).save(any(ProductContributor.class));
	}

	@Test
	@DisplayName("create product - fail1")
	void create_product_fail1_test() {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		when(productJpaRepository.existsByProductIsbn(any())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(ProductAlreadyExistsException.class);
	}

	@Test
	@DisplayName("create product - fail2")
	void create_product_fail2_test() {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		when(productJpaRepository.existsByProductIsbn(any())).thenReturn(false);
		when(productStateJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(ProductStateNotFoundException.class);
	}

	@Test
	@DisplayName("create product - fail3")
	void create_product_fail3_test() {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		when(productJpaRepository.existsByProductIsbn(any())).thenReturn(false);
		when(productStateJpaRepository.findById(anyLong())).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(PublisherNotFoundException.class);
	}

	@Test
	@DisplayName("create product - fail4")
	void create_product_fail4_test() {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("Publisher");
		when(productJpaRepository.existsByProductIsbn(any())).thenReturn(false);
		when(productStateJpaRepository.findById(anyLong())).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.of(publisher));
		when(tagJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(TagNotFoundException.class);
	}

	@Test
	@DisplayName("create product - fail5")
	void create_product_fail5_test() {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("Publisher");
		Tag tag = new Tag("tag A");
		when(productJpaRepository.existsByProductIsbn(any())).thenReturn(false);
		when(productStateJpaRepository.findById(anyLong())).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.of(publisher));
		when(tagJpaRepository.findById(anyLong())).thenReturn(Optional.of(tag));
		when(contributorJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(ContributorNotFoundException.class);
	}

	@Test
	@DisplayName("get product - success")
	void get_product_success_test() {
		// given
		long productId = 1L;
		Product product = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());

		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productImageJpaRepository.findImageDTOsByProductId(productId)).thenReturn(List.of());
		when(productTagJpaRepository.findTagDTOsByProductId(productId)).thenReturn(List.of());
		when(productCategoryJpaRepository.findCategoryDTOsByProductId(productId)).thenReturn(List.of());
		when(productContributorJpaRepository.findContributorDTOsByProductId(productId)).thenReturn(List.of());

		// when
		ResponseProductReadDTO response = productService.getProduct(productId);

		// then
		assertEquals("Product1", response.getProductTitle());
		assertEquals(8000, response.getProductSalePrice());
	}

	@Test
	@DisplayName("get product - fail")
	void get_product_fail_test() {
		// given
		long productId = 1L;
		when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.getProduct(productId))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("get products - success1")
	void get_products_success1_test() {
		// given
		long categoryId = 0L;
		Product product1 = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		Product product2 = new Product(
			2L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product2", "Product2 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-2", 7000, 6000,
			true, 100, 0, 0, new ArrayList<>());
		List<Product> products = List.of(product1, product2);

		Pageable pageable = PageRequest.of(0, 9);
		Page<Product> productPage = new PageImpl<>(products);

		when(productJpaRepository.findAll(pageable)).thenReturn(productPage);
		when(productImageJpaRepository.findAllByProductIdsGrouped(any())).thenReturn(Map.of());
		when(productTagJpaRepository.findTagsGroupedByProductIds(any())).thenReturn(Map.of());
		when(productCategoryJpaRepository.findCategoriesGroupedByProductIds(any())).thenReturn(Map.of());
		when(productContributorJpaRepository.findContributorsGroupedByProductIds(any())).thenReturn(Map.of());

		// when
		Page<ResponseProductReadDTO> result = productService.getProducts(pageable, categoryId);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Product1");
		assertThat(result.getContent().get(1).getProductTitle()).isEqualTo("Product2");
	}

	@Test
	@DisplayName("get products - success2")
	void get_products_success2_test() {
		// given
		long categoryId = 1L;
		Product product1 = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		Product product2 = new Product(
			2L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product2", "Product2 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-2", 7000, 6000,
			true, 100, 0, 0, new ArrayList<>());
		List<Product> products = List.of(product1);

		Pageable pageable = PageRequest.of(0, 9);
		Page<Product> productPage = new PageImpl<>(products);

		when(productJpaRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(productPage);
		when(productImageJpaRepository.findAllByProductIdsGrouped(any())).thenReturn(Map.of());
		when(productTagJpaRepository.findTagsGroupedByProductIds(any())).thenReturn(Map.of());
		when(productCategoryJpaRepository.findCategoriesGroupedByProductIds(any())).thenReturn(Map.of());
		when(productContributorJpaRepository.findContributorsGroupedByProductIds(any())).thenReturn(Map.of());

		// when
		Page<ResponseProductReadDTO> result = productService.getProducts(pageable, categoryId);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Product1");
	}

	@Test
	@DisplayName("get products (order) - success")
	void get_products_success_test() {
		// given
		List<Long> productIds = List.of(1L, 2L);
		Product product1 = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		Product product2 = new Product(
			2L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product2", "Product2 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-2", 7000, 6000,
			true, 100, 0, 0, new ArrayList<>());
		List<Product> products = List.of(product1, product2);

		when(productJpaRepository.findAllById(productIds)).thenReturn(products);
		when(productImageJpaRepository.findImageDTOsByProductId(anyLong())).thenReturn(List.of());
		when(productTagJpaRepository.findTagDTOsByProductId(anyLong())).thenReturn(List.of());
		when(productCategoryJpaRepository.findCategoryDTOsByProductId(anyLong())).thenReturn(List.of());
		when(productContributorJpaRepository.findContributorDTOsByProductId(anyLong())).thenReturn(List.of());

		// when
		List<ResponseProductReadDTO> result = productService.getProducts(productIds);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getProductTitle()).isEqualTo("Product1");
		assertThat(result.get(1).getProductTitle()).isEqualTo("Product2");
	}

	@Test
	@DisplayName("get products (order) - fail")
	void get_products_fail_test() {
		// given
		List<Long> productIds = List.of(1L, 2L);
		Product product1 = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		List<Product> products = List.of(product1);

		when(productJpaRepository.findAllById(productIds)).thenReturn(products);

		// when & then
		assertThatThrownBy(() -> productService.getProducts(productIds))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product - success")
	void update_product_success_test() {
		// given
		long productId = 1L;
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("publisher");
		Tag tag = new Tag("tag A");
		Position position = new Position("writer");
		Contributor contributor = new Contributor("conA", position);
		Product product = new Product(
			1L, productState, publisher, "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productStateJpaRepository.findById(1L)).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(tagJpaRepository.findById(1L)).thenReturn(Optional.of(tag));
		when(contributorJpaRepository.findById(1L)).thenReturn(Optional.of(contributor));

		// when
		productService.updateProduct(productId, request);

		// then
		verify(productJpaRepository, times(1)).save(any(Product.class));
		verify(productImageJpaRepository, times(2)).save(any());
		verify(productTagJpaRepository, times(1)).save(any(ProductTag.class));
		verify(productContributorJpaRepository, times(1)).save(any(ProductContributor.class));
		verify(productImageJpaRepository, times(1)).deleteByProduct_ProductId(productId);
		verify(productTagJpaRepository, times(1)).deleteByProduct_ProductId(productId);
		verify(productContributorJpaRepository, times(1)).deleteByProduct_ProductId(productId);
	}

	@Test
	@DisplayName("update product - fail1")
	void update_product_fail1_test() {
		// given
		long productId = 1L;
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(productId, request))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product - fail2")
	void update_product_fail2_test() {
		// given
		long productId = 1L;
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("publisher");
		Product product = new Product(
			1L, productState, publisher, "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productStateJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(productId, request))
			.isInstanceOf(ProductStateNotFoundException.class);
	}

	@Test
	@DisplayName("update product - fail3")
	void update_product_fail3_test() {
		// given
		long productId = 1L;
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("publisher");
		Product product = new Product(
			1L, productState, publisher, "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productStateJpaRepository.findById(1L)).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(productId, request))
			.isInstanceOf(PublisherNotFoundException.class);
	}

	@Test
	@DisplayName("update product - fail4")
	void update_product_fail4_test() {
		// given
		long productId = 1L;
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("publisher");
		Product product = new Product(
			1L, productState, publisher, "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productStateJpaRepository.findById(1L)).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(tagJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(productId, request))
			.isInstanceOf(TagNotFoundException.class);
	}

	@Test
	@DisplayName("update product - fail5")
	void update_product_fail5_test() {
		// given
		long productId = 1L;
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		ProductState productState = new ProductState(ProductStateName.SALE);
		Publisher publisher = new Publisher("publisher");
		Tag tag = new Tag("tag A");
		Product product = new Product(
			1L, productState, publisher, "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productStateJpaRepository.findById(1L)).thenReturn(Optional.of(productState));
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(tagJpaRepository.findById(1L)).thenReturn(Optional.of(tag));
		when(contributorJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(productId, request))
			.isInstanceOf(ContributorNotFoundException.class);
	}

	@Test
	@DisplayName("update product stock - success")
	void update_productStock_success_test() {
		// given
		long productId = 1L;
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(50);
		Product product = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		productService.updateProductStock(productId, request);

		// then
		assertEquals(150, product.getProductStock());
	}

	@Test
	@DisplayName("update product stock - fail1")
	void update_productStock_fail1_test() {
		// given
		long productId = 1L;
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(50);
		when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProductStock(productId, request))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product stock - fail2")
	void update_productStock_fail2_test() {
		// given
		long productId = 1L;
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(-200);
		Product product = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

		// when & then
		assertThatThrownBy(() -> productService.updateProductStock(productId, request))
			.isInstanceOf(ProductStockDecrementException.class);
	}

	@Test
	@DisplayName("update product sale price - success")
	void update_productSalePrice_success_test() {
		// given
		long productId = 1L;
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(5000);
		Product product = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		productService.updateProductSalePrice(productId, request);

		// then
		assertEquals(5000, product.getProductSalePrice());
	}

	@Test
	@DisplayName("update product sale price - fail")
	void update_productSalePrice_fail_test() {
		long productId = 1L;
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(5000);
		when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.updateProductSalePrice(productId, request))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("get products to coupon")
	void get_products_to_coupon_test() {
		// given
		Product product1 = new Product(
			1L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product1", "Product1 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-1", 10000, 8000,
			true, 100, 0, 0, new ArrayList<>());
		Product product2 = new Product(
			2L, new ProductState(ProductStateName.SALE), new Publisher("publisher"), "Product2", "Product2 content",
			"Product1 description", LocalDate.now(), "978-89-12345-01-2", 7000, 6000,
			true, 100, 0, 0, new ArrayList<>());
		List<Product> products = List.of(product1, product2);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> productPage = new PageImpl<>(products);

		when(productJpaRepository.findAllByProductStateName(ProductStateName.SALE, pageable)).thenReturn(productPage);

		// when
		Page<ResponseProductCouponDTO> result = productService.getProductsToCoupon(pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Product1");
		assertThat(result.getContent().get(1).getProductTitle()).isEqualTo("Product2");
	}
}
