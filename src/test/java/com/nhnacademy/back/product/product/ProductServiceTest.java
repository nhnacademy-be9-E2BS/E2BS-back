// package com.nhnacademy.back.product.product;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
//
// import com.nhnacademy.back.product.category.domain.entity.Category;
// import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
// import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
// import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
// import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
// import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;
// import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
// import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
// import com.nhnacademy.back.product.image.domain.entity.ProductImage;
// import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
// import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
// import com.nhnacademy.back.product.product.domain.dto.request.RequestProductGetDTO;
// import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
// import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
// import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
// import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
// import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
// import com.nhnacademy.back.product.product.domain.entity.Product;
// import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
// import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
// import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
// import com.nhnacademy.back.product.product.kim.service.impl.ProductServiceImpl;
// import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
// import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
// import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
// import com.nhnacademy.back.product.state.domain.entity.ProductState;
// import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
// import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
// import com.nhnacademy.back.product.tag.domain.entity.ProductTag;
// import com.nhnacademy.back.product.tag.domain.entity.Tag;
// import com.nhnacademy.back.product.tag.repository.ProductTagJpaRepository;
// import com.nhnacademy.back.product.tag.repository.TagJpaRepository;
//
// @ExtendWith(MockitoExtension.class)
// class ProductServiceTest {
//
// 	@InjectMocks
// 	private ProductServiceImpl productService;
//
// 	@Mock
// 	private ProductJpaRepository productJpaRepository;
//
// 	@Mock
// 	private ProductImageJpaRepository productImageJpaRepository;
//
// 	@Mock
// 	private PublisherJpaRepository publisherJpaRepository;
//
// 	@Mock
// 	private ProductStateJpaRepository productStateJpaRepository;
//
// 	@Mock
// 	private CategoryJpaRepository categoryJpaRepository;
//
// 	@Mock
// 	private ProductCategoryJpaRepository productCategoryJpaRepository;
//
// 	@Mock
// 	private ContributorJpaRepository contributorJpaRepository;
//
// 	@Mock
// 	private ProductContributorJpaRepository productContributorJpaRepository;
//
// 	@Mock
// 	private TagJpaRepository tagJpaRepository;
//
// 	@Mock
// 	private ProductTagJpaRepository productTagJpaRepository;
//
// 	@Test
// 	@DisplayName("도서 생성 - 성공")
// 	void createProductSuccess() {
// 		// given
// 		RequestProductCreateDTO request = new RequestProductCreateDTO(
// 			"Test Publisher", "Test Book", "Content", "Description", "1234567890123",
// 			10000L, 9000L, true, 50, List.of("image1.jpg"), List.of("tag1"), List.of(1L), List.of("Contributor1")
// 		);
// 		Publisher publisher = new Publisher("Test Publisher");
// 		Tag tag = new Tag("tag1");
// 		Category category = new Category("Test Category", null);
// 		Contributor contributor = new Contributor("Contributor1", null);
// 		Product product = Product.createProductEntity(request, publisher);
//
// 		when(publisherJpaRepository.findByPublisherName("Test Publisher")).thenReturn(publisher);
// 		when(productJpaRepository.existsByProductIsbn("1234567890123")).thenReturn(false);
// 		when(productJpaRepository.save(any(Product.class))).thenReturn(product);
// 		when(tagJpaRepository.findByTagName("tag1")).thenReturn(Optional.of(tag));
// 		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));
// 		when(contributorJpaRepository.findByContributorName("Contributor1")).thenReturn(Optional.of(contributor));
//
// 		// when
// 		productService.createProduct(request);
//
// 		// then
// 		verify(productJpaRepository, times(1)).save(any(Product.class));
// 		verify(productImageJpaRepository, times(1)).save(any(ProductImage.class));
// 		verify(productTagJpaRepository, times(1)).save(any(ProductTag.class));
// 		verify(productCategoryJpaRepository, times(1)).save(any(ProductCategory.class));
// 		verify(productContributorJpaRepository, times(1)).save(any(ProductContributor.class));
// 	}
//
// 	@Test
// 	@DisplayName("도서 생성 - 실패 (이미 존재)")
// 	void createProductFailAlreadyExists() {
// 		// given
// 		RequestProductCreateDTO request = new RequestProductCreateDTO(
// 			"Test Publisher", "Test Book", "Content", "Description", "1234567890123",
// 			10000L, 9000L, true, 50, List.of("image1.jpg"), List.of("tag1"), List.of(1L), List.of("Contributor1")
// 		);
// 		Publisher publisher = new Publisher("Test Publisher");
// 		Tag tag = new Tag("tag1");
// 		Category category = new Category("Test Category", null);
// 		Contributor contributor = new Contributor("Contributor1", null);
//
// 		// 첫 번째 생성 시 ISBN 존재하지 않음 (성공적으로 생성)
// 		when(productJpaRepository.existsByProductIsbn("1234567890123")).thenReturn(false, true); // 첫 번째는 false, 두 번째는 true
// 		when(publisherJpaRepository.findByPublisherName("Test Publisher")).thenReturn(publisher);
// 		when(tagJpaRepository.findByTagName("tag1")).thenReturn(Optional.of(tag));
// 		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));
// 		when(contributorJpaRepository.findByContributorName("Contributor1")).thenReturn(Optional.of(contributor));
// 		when(productJpaRepository.save(any(Product.class))).thenReturn(Product.createProductEntity(request, publisher));
//
// 		// 첫 번째 도서 생성
// 		productService.createProduct(request);
//
// 		// 두 번째 도서 생성 시 ISBN이 이미 존재하므로 예외 발생
// 		assertThatThrownBy(() -> productService.createProduct(request))
// 			.isInstanceOf(ProductAlreadyExistsException.class)
// 			.hasMessage("Product already exists");
//
// 		// then
// 		verify(productJpaRepository, times(2)).existsByProductIsbn("1234567890123");
// 		verify(productJpaRepository, times(1)).save(any(Product.class)); // 한 번만 저장됨
// 	}
// 	@Test
// 	@DisplayName("도서 단건 조회 - 성공")
// 	void getProductSuccess() {
// 		// given
// 		RequestProductGetDTO request = new RequestProductGetDTO("1234567890123");
// 		Publisher publisher = new Publisher("Test Publisher");
// 		ProductState productState = new ProductState(ProductStateName.SALE);
// 		Product product = Product.builder()
// 			.productId(1L)
// 			.productTitle("Test Book")
// 			.productContent("Content")
// 			.productDescription("Description")
// 			.productIsbn("1234567890123")
// 			.productRegularPrice(10000L)
// 			.productSalePrice(9000L)
// 			.productPackageable(true)
// 			.productStock(50)
// 			.productPublishedAt(LocalDate.now())
// 			.publisher(publisher)
// 			.productState(productState)
// 			.build();
// 		Tag tag = new Tag("tag1");
// 		ProductTag productTag = new ProductTag(product, tag);
// 		Category category = new Category("Test Category", null);
// 		ProductCategory productCategory = new ProductCategory(product, category);
// 		Contributor contributor = new Contributor("Contributor1", null);
// 		ProductContributor productContributor = new ProductContributor(product, contributor);
//
// 		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
// 		when(productImageJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(new ProductImage(product, "image1.jpg")));
// 		when(productTagJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(productTag));
// 		when(productCategoryJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(productCategory));
// 		when(productContributorJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(productContributor));
//
// 		// when
// 		ResponseProductReadDTO result = productService.getProduct(1L, request);
//
// 		// then
// 		assertThat(result.getProductTitle()).isEqualTo("Test Book");
// 		assertThat(result.getProductImagePaths()).containsExactly("image1.jpg");
// 		assertThat(result.getTagNames()).containsExactly("tag1");
// 		assertThat(result.getCategoryIds()).containsExactly(category.getCategoryId());
// 		assertThat(result.getContributorNames()).containsExactly("Contributor1");
// 		assertThat(result.getProductSalePrice()).isEqualTo(9000L);
// 	}
//
// 	@Test
// 	@DisplayName("도서 단건 조회 - 실패 (없음)")
// 	void getProductFailNotFound() {
// 		// given
// 		RequestProductGetDTO request = new RequestProductGetDTO("1234567890123");
// 		when(productJpaRepository.findById(1L)).thenReturn(Optional.empty());
//
// 		// when & then
// 		assertThatThrownBy(() -> productService.getProduct(1L, request))
// 			.isInstanceOf(ProductNotFoundException.class);
// 	}
//
// 	@Test
// 	@DisplayName("도서 목록 조회 - 성공")
// 	void getProductsSuccess() {
// 		// given
// 		Pageable pageable = PageRequest.of(0, 10);
// 		Publisher publisher = new Publisher("Test Publisher");
// 		ProductState productState = new ProductState(ProductStateName.SALE);
// 		Product product = Product.builder()
// 			.productId(1L)
// 			.productTitle("Test Book")
// 			.productContent("Content")
// 			.productDescription("Description")
// 			.productIsbn("1234567890123")
// 			.productRegularPrice(10000L)
// 			.productSalePrice(9000L)
// 			.productPackageable(true)
// 			.productStock(50)
// 			.productPublishedAt(LocalDate.now())
// 			.publisher(publisher)
// 			.productState(productState)
// 			.build();
// 		Tag tag = new Tag("tag1");
// 		ProductTag productTag = new ProductTag(product, tag);
// 		Category category = new Category("Test Category", null);
// 		ProductCategory productCategory = new ProductCategory(product, category);
// 		Contributor contributor = new Contributor("Contributor1", null);
// 		ProductContributor productContributor = new ProductContributor(product, contributor);
//
// 		Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
// 		when(productJpaRepository.findAll(pageable)).thenReturn(page);
// 		when(productImageJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(new ProductImage(product, "image1.jpg")));
// 		when(productTagJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(productTag));
// 		when(productCategoryJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(productCategory));
// 		when(productContributorJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(productContributor));
//
// 		// when
// 		Page<ResponseProductReadDTO> result = productService.getProducts(pageable);
//
// 		// then
// 		assertThat(result.getContent()).hasSize(1);
// 		assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Test Book");
// 		assertThat(result.getContent().get(0).getTagNames()).containsExactly("tag1");
// 		assertThat(result.getContent().get(0).getCategoryIds()).containsExactly(category.getCategoryId());
// 		assertThat(result.getContent().get(0).getContributorNames()).containsExactly("Contributor1");
// 		assertThat(result.getContent().get(0).getProductSalePrice()).isEqualTo(9000L);
// 	}
//
// 	@Test
// 	@DisplayName("도서 수정 - 성공")
// 	void updateProductSuccess() {
// 		// given
// 		RequestProductUpdateDTO request = new RequestProductUpdateDTO(
// 			"SALE", "Test Publisher", "Updated Book", "Updated Content", "Updated Description",
// 			12000L, 11000L, true, 60, List.of("image2.jpg"), List.of("tag2"), List.of(2L), List.of("Contributor2")
// 		);
// 		Publisher publisher = new Publisher("Test Publisher");
// 		ProductState productState = new ProductState(ProductStateName.SALE);
// 		Product product = Product.builder()
// 			.productId(1L)
// 			.productTitle("Test Book")
// 			.productContent("Content")
// 			.productDescription("Description")
// 			.productRegularPrice(10000L)
// 			.productSalePrice(9000L)
// 			.productPackageable(true)
// 			.productStock(50)
// 			.publisher(publisher)
// 			.productState(productState)
// 			.build();
// 		Tag tag = new Tag("tag2");
// 		Category category = new Category("Updated Category", null);
// 		Contributor contributor = new Contributor("Contributor2", null);
//
// 		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
// 		when(publisherJpaRepository.findByPublisherName("Test Publisher")).thenReturn(publisher);
// 		when(productStateJpaRepository.findByProductStateName(ProductStateName.SALE)).thenReturn(productState);
// 		when(tagJpaRepository.findByTagName("tag2")).thenReturn(Optional.of(tag));
// 		when(categoryJpaRepository.findById(2L)).thenReturn(Optional.of(category));
// 		when(contributorJpaRepository.findByContributorName("Contributor2")).thenReturn(Optional.of(contributor));
//
// 		// when
// 		productService.updateProduct(1L, request);
//
// 		// then
// 		verify(productJpaRepository, times(1)).save(product);
// 		verify(productImageJpaRepository, times(1)).deleteByProduct_ProductId(1L);
// 		verify(productImageJpaRepository, times(1)).save(any(ProductImage.class));
// 		verify(productTagJpaRepository, times(1)).deleteByProduct_ProductId(1L);
// 		verify(productTagJpaRepository, times(1)).save(any(ProductTag.class));
// 		verify(productCategoryJpaRepository, times(1)).deleteByProduct_ProductId(1L);
// 		verify(productCategoryJpaRepository, times(1)).save(any(ProductCategory.class));
// 		verify(productContributorJpaRepository, times(1)).deleteByProduct_ProductId(1L);
// 		verify(productContributorJpaRepository, times(1)).save(any(ProductContributor.class));
// 		assertThat(product.getProductTitle()).isEqualTo("Updated Book");
// 		assertThat(product.getProductSalePrice()).isEqualTo(11000L);
// 	}
//
// 	@Test
// 	@DisplayName("도서 재고 수정 - 성공")
// 	void updateProductStockSuccess() {
// 		// given
// 		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(10);
// 		Product product = Product.builder()
// 			.productId(1L)
// 			.productStock(50)
// 			.build();
// 		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
//
// 		// when
// 		productService.updateProductStock(1L, request);
//
// 		// then
// 		verify(productJpaRepository, times(1)).save(product);
// 		assertThat(product.getProductStock()).isEqualTo(40);
// 	}
//
// 	@Test
// 	@DisplayName("도서 재고 수정 - 실패 (재고 부족)")
// 	void updateProductStockFailInsufficientStock() {
// 		// given
// 		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(60);
// 		Product product = Product.builder()
// 			.productId(1L)
// 			.productStock(50)
// 			.build();
// 		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
//
// 		// when & then
// 		assertThatThrownBy(() -> productService.updateProductStock(1L, request))
// 			.isInstanceOf(ProductStockDecrementException.class);
// 	}
//
// 	@Test
// 	@DisplayName("도서 판매가 수정 - 성공")
// 	void updateProductSalePriceSuccess() {
// 		// given
// 		long productId = 1L;
// 		long newSalePrice = 9500L;
// 		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(newSalePrice);
// 		Product product = Product.builder()
// 			.productId(productId)
// 			.productSalePrice(9000)
// 			.build();
//
// 		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
//
// 		// when
// 		productService.updateProductSalePrice(productId, request);
//
// 		// then
// 		verify(productJpaRepository, times(1)).findById(productId);
// 		verify(productJpaRepository, times(1)).save(product);
// 		assertThat(product.getProductSalePrice()).isEqualTo(newSalePrice);
// 	}
//
// 	@Test
// 	@DisplayName("쿠폰 적용 가능 도서 조회 - 성공")
// 	void getProductsForCouponSuccess() {
// 		// given
// 		Pageable pageable = PageRequest.of(0, 10);
// 		Publisher publisher = new Publisher("Test Publisher");
// 		ProductState productState = new ProductState(ProductStateName.SALE);
// 		Product product = Product.builder()
// 			.productId(1L)
// 			.productTitle("Test Book")
// 			.publisher(publisher)
// 			.productState(productState)
// 			.build();
// 		Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
// 		when(productJpaRepository.findAllByProductStateName(ProductStateName.SALE, pageable)).thenReturn(page);
//
// 		// when
// 		Page<ResponseProductCouponDTO> result = productService.getProductsToCoupon(pageable);
//
// 		// then
// 		assertThat(result.getContent()).hasSize(1);
// 		assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Test Book");
// 	}
// }