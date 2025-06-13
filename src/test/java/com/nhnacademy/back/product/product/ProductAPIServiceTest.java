package com.nhnacademy.back.product.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
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
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.exception.ProductCategoryCreateNotAllowException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.api.AladdinOpenAPI;
import com.nhnacademy.back.product.product.api.Item;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateByQueryDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchByQueryTypeDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductApiSearchByQueryTypeDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.SearchBookException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.impl.ProductAPIServiceImpl;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.PublisherService;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.repository.ProductTagJpaRepository;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;


@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ProductAPIServiceTest {

	@Mock ProductJpaRepository productJpaRepository;
	@Mock PublisherJpaRepository publisherJpaRepository;
	@Mock ProductImageJpaRepository productImageJpaRepository;
	@Mock ContributorJpaRepository contributorJpaRepository;
	@Mock PositionJpaRepository positionJpaRepository;
	@Mock PublisherService publisherService;
	@Mock ProductContributorJpaRepository productContributorJpaRepository;
	@Mock ProductStateJpaRepository productStateJpaRepository;
	@Mock ProductCategoryJpaRepository productCategoryJpaRepository;
	@Mock ProductTagJpaRepository productTagJpaRepository;
	@Mock CategoryJpaRepository categoryJpaRepository;
	@Mock TagJpaRepository tagJpaRepository;
	@Mock ProductSearchService productSearchService;

	@InjectMocks
	ProductAPIServiceImpl service;

	@Test
	@DisplayName("searchProducts 성공")
	void searchProducts_success() {
		RequestProductApiSearchDTO req = mock(RequestProductApiSearchDTO.class);
		when(req.getQuery()).thenReturn("keyword");
		when(req.getQueryType()).thenReturn("Title");

		Item item = new Item();
		item.setIsbn13("12345");
		item.setTitle("Test Book");
		item.setDescription("Desc");
		item.setCover("CoverUrl");
		item.setPublisher("Pub");
		item.setPriceStandard(2000);
		item.setPriceSales(1500);
		item.setAuthor("Author");
		item.setPubDate(LocalDate.of(2020, 1, 1));

		List<Item> items = List.of(item);

		try (MockedConstruction<AladdinOpenAPI> mockCtor =
				 mockConstruction(AladdinOpenAPI.class, (mockApi, ctx) -> {
					 when(mockApi.searchBooks()).thenReturn(items);
				 })) {
			Pageable pageReq = PageRequest.of(0, 10);
			Page<ResponseProductsApiSearchDTO> page = service.searchProducts(req, pageReq);

			assertThat(page.getTotalElements()).isEqualTo(1);
			assertThat(page.getContent().get(0).getProductTitle()).isEqualTo("Test Book");
		}
	}

	@Test
	@DisplayName("searchProducts API 실패시 SearchBookException")
	void searchProducts_apiFailure() {
		RequestProductApiSearchDTO req = mock(RequestProductApiSearchDTO.class);
		when(req.getQuery()).thenReturn("k");
		when(req.getQueryType()).thenReturn("Type");

		try (MockedConstruction<AladdinOpenAPI> mockCtor =
				 mockConstruction(AladdinOpenAPI.class, (mockApi, ctx) -> {
					 when(mockApi.searchBooks()).thenThrow(new RuntimeException("fail API"));
				 })) {
			assertThatThrownBy(() -> service.searchProducts(req, PageRequest.of(0,5)))
				.isInstanceOf(SearchBookException.class)
				.hasMessageContaining("Search book failed");
		}
	}

	@Test
	@DisplayName("searchProductsByQuery 성공")
	void searchProductsByQuery_success() {
		RequestProductApiSearchByQueryTypeDTO req = mock(RequestProductApiSearchByQueryTypeDTO.class);
		when(req.getQueryType()).thenReturn("Bestseller");

		Item item = new Item();
		item.setIsbn13("abc");
		item.setTitle("BS Book");
		List<Item> items = List.of(item);

		try (MockedConstruction<AladdinOpenAPI> mockCtor =
				 mockConstruction(AladdinOpenAPI.class, (mockApi, ctx) -> {
					 when(mockApi.getListBooks()).thenReturn(items);
				 })) {
			Page<ResponseProductApiSearchByQueryTypeDTO> page =
				service.searchProductsByQuery(req, PageRequest.of(0,3));

			assertThat(page.getTotalElements()).isEqualTo(1);
			assertThat(page.getContent().get(0).getProductTitle()).isEqualTo("BS Book");
		}
	}

	@Test
	@DisplayName("createProduct 중복 ISBN 예외")
	void createProduct_duplicateThrows() {
		RequestProductApiCreateDTO req = mock(RequestProductApiCreateDTO.class);
		when(req.getPublisherName()).thenReturn("Pub");
		when(req.getProductIsbn()).thenReturn("dupIsbn");

		when(publisherJpaRepository.findByPublisherName("Pub")).thenReturn(new Publisher("Pub"));
		when(productJpaRepository.existsByProductIsbn("dupIsbn")).thenReturn(true);

		assertThatThrownBy(() -> service.createProduct(req))
			.isInstanceOf(ProductAlreadyExistsException.class)
			.hasMessageContaining("Product already exists");
	}


	@Test
	@DisplayName("createProductByQuery 중복 예외")
	void createByQuery_duplicateThrows() {
		RequestProductApiCreateByQueryDTO req = mock(RequestProductApiCreateByQueryDTO.class);
		when(req.getPublisherName()).thenReturn("P");
		when(req.getProductIsbn()).thenReturn("dup");
		when(publisherJpaRepository.findByPublisherName("P")).thenReturn(new Publisher("P"));
		when(productJpaRepository.existsByProductIsbn("dup")).thenReturn(true);

		assertThatThrownBy(() -> service.createProductByQuery(req))
			.isInstanceOf(ProductAlreadyExistsException.class);
	}

	@Test
	void searchProductsByQuery_apiThrows() {
		RequestProductApiSearchByQueryTypeDTO req = mock(RequestProductApiSearchByQueryTypeDTO.class);
		when(req.getQueryType()).thenReturn("XYZ");
		try (var mc = mockConstruction(AladdinOpenAPI.class, (m, c) ->
			when(m.getListBooks()).thenThrow(new RuntimeException("fail"))
		)) {
			assertThatThrownBy(() -> service.searchProductsByQuery(req, PageRequest.of(0,1)))
				.isInstanceOf(SearchBookException.class)
				.hasMessageContaining("Search book failed");
		}
	}


	@Test
	void parse_util() throws Exception {
		Method parse = ProductAPIServiceImpl.class.getDeclaredMethod("parse", String.class);
		parse.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<String,String> map1 = (Map<String,String>)parse.invoke(service, "Alice(Dev), Bob");
		assertThat(map1).containsEntry("Alice","Dev")
			.containsEntry("Bob","없음");
	}

	// 7. createProductByQuery – queryType null
	@Test
	@DisplayName("createProductByQuery queryType null 시 IllegalArgumentException")
	void createByQuery_queryTypeNull() {
		RequestProductApiCreateByQueryDTO req = mock(RequestProductApiCreateByQueryDTO.class);
		when(req.getPublisherName()).thenReturn("P");
		when(req.getProductIsbn()).thenReturn("I1");
		when(req.getProductImage()).thenReturn("dummy.jpg");
		when(req.getContributors()).thenReturn("Bob(Editor)");
		when(req.getCategoryIds()).thenReturn(List.of(1L));
		when(req.getTagIds()).thenReturn(null);
		when(req.getQueryType()).thenReturn(null);

		when(publisherJpaRepository.findByPublisherName("P"))
			.thenReturn(null)
			.thenReturn(new Publisher("P"));
		doNothing().when(publisherService).createPublisher(any());

		when(productJpaRepository.existsByProductIsbn("I1")).thenReturn(false);
		when(productStateJpaRepository.findByProductStateName(any()))
			.thenReturn(null)
			.thenReturn(new ProductState(ProductStateName.SALE));
		when(productStateJpaRepository.save(any()))
			.thenReturn(new ProductState(ProductStateName.SALE));

		Product dummy = createForTest(42L, "T","D", LocalDate.now(), 123);
		when(productJpaRepository.save(any())).thenReturn(dummy);

		when(positionJpaRepository.existsByPositionName(any())).thenReturn(false);
		when(positionJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(positionJpaRepository.findPositionByPositionName("Editor"))
			.thenReturn(new Position("Editor"));
		when(contributorJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(productContributorJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		Category c1 = new Category( "C1", null);
		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(c1));
		when(productCategoryJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(productCategoryJpaRepository.findCategoryIdsByProductId(42L))
			.thenReturn(List.of(1L));

		doNothing().when(productSearchService).createProductDocument(any());

		assertThatThrownBy(() -> service.createProductByQuery(req))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("QueryType is null");
	}

	@Test
	@DisplayName("createProduct 정상 흐름 전체")
	void createProduct_fullSuccess() {
		RequestProductApiCreateDTO req = mock(RequestProductApiCreateDTO.class);
		when(req.getPublisherName()).thenReturn("NewPub");
		when(req.getProductIsbn()).thenReturn("ISBN1");
		when(req.getProductImage()).thenReturn("img.jpg");
		when(req.getContributors()).thenReturn("Alice(Dev),Bob(Tester)");
		when(req.getCategoryIds()).thenReturn(List.of(1L, 2L));
		when(req.getTagIds()).thenReturn(List.of(10L));

		when(publisherJpaRepository.findByPublisherName("NewPub"))
			.thenReturn(null)
			.thenReturn(new Publisher("NewPub"));
		doNothing().when(publisherService).createPublisher(any(RequestPublisherDTO.class));

		when(productJpaRepository.existsByProductIsbn("ISBN1")).thenReturn(false);

		when(productStateJpaRepository.findByProductStateName(ProductStateName.SALE))
			.thenReturn(null);
		when(productStateJpaRepository.save(any()))
			.thenReturn(new ProductState(ProductStateName.SALE));

		Product saved = createForTest(100L, "T","D", LocalDate.now(), 123);
		when(productJpaRepository.save(any())).thenReturn(saved);

		when(positionJpaRepository.existsByPositionName(anyString())).thenReturn(false);
		when(positionJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(positionJpaRepository.findPositionByPositionName("Dev"))
			.thenReturn(new Position("Dev"));
		when(contributorJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(productContributorJpaRepository.save(any(ProductContributor.class)))
			.thenAnswer(i -> i.getArgument(0));

		Category c1 = new Category("1" , null);
		Category c2 = new Category("2", c1);
		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(c1));
		when(categoryJpaRepository.findById(2L)).thenReturn(Optional.of(c2));
		when(productCategoryJpaRepository.save(any(ProductCategory.class)))
			.thenAnswer(i -> i.getArgument(0));
		when(productCategoryJpaRepository.findCategoryIdsByProductId(100L))
			.thenReturn(List.of(1L,2L));

		Tag t10 = new Tag( "Tag10");
		when(tagJpaRepository.findById(10L)).thenReturn(Optional.of(t10));
		when(productTagJpaRepository.save(any()))
			.thenAnswer(i -> i.getArgument(0));

		doNothing().when(productSearchService).createProductDocument(any(RequestProductDocumentDTO.class));

		service.createProduct(req);

		verify(publisherService).createPublisher(any());
		verify(productStateJpaRepository).save(any());
		verify(productImageJpaRepository).save(any(ProductImage.class));
		verify(productCategoryJpaRepository, times(2)).save(any(ProductCategory.class));
		verify(productSearchService).createProductDocument(any());
	}

	@Test
	@DisplayName("createProduct 카테고리 개수 제한 예외")
	void createProduct_categoryLimit() {
		RequestProductApiCreateDTO req = mock(RequestProductApiCreateDTO.class);
		when(req.getPublisherName()).thenReturn("P");
		when(req.getProductIsbn()).thenReturn("I1");
		when(req.getProductImage()).thenReturn("dummy.jpg");      // image null 방지
		when(req.getContributors()).thenReturn("Alice(Dev)");   // parse null 방지

		when(req.getCategoryIds()).thenReturn(List.of());

		when(productJpaRepository.existsByProductIsbn("I1")).thenReturn(false);
		when(publisherJpaRepository.findByPublisherName("P")).thenReturn(new Publisher("P"));
		when(productStateJpaRepository.findByProductStateName(any()))
			.thenReturn(new ProductState(ProductStateName.SALE));
		when(productJpaRepository.save(any()))
			.thenReturn(createForTest(1L,"","",LocalDate.now(),0));

		assertThatThrownBy(() -> service.createProduct(req))
			.isInstanceOf(ProductCategoryCreateNotAllowException.class);
	}


	@Test
	@DisplayName("createProductByQuery tagIds null 분기(정상 처리)")
	void createByQuery_tagIdsNull() {
		RequestProductApiCreateByQueryDTO req = mock(RequestProductApiCreateByQueryDTO.class);
		when(req.getPublisherName()).thenReturn("PQ");
		when(req.getProductIsbn()).thenReturn("IQ");
		when(req.getQueryType()).thenReturn("QT");
		when(req.getContributors()).thenReturn("X(Y)");
		when(req.getCategoryIds()).thenReturn(List.of(5L));
		when(req.getTagIds()).thenReturn(null);  // null 태그

		when(publisherJpaRepository.findByPublisherName("PQ"))
			.thenReturn(null)
			.thenReturn(new Publisher("PQ"));
		doNothing().when(publisherService).createPublisher(any());
		when(productJpaRepository.existsByProductIsbn("IQ")).thenReturn(false);
		when(productStateJpaRepository.findByProductStateName(any()))
			.thenReturn(null)
			.thenReturn(new ProductState(ProductStateName.SALE));
		when(productStateJpaRepository.save(any()))
			.thenReturn(new ProductState(ProductStateName.SALE));
		Product prod = createForTest(200L, "TQ","DQ", LocalDate.now(), 200);
		when(productJpaRepository.save(any())).thenReturn(prod);

		when(positionJpaRepository.existsByPositionName(any())).thenReturn(false);
		when(positionJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(positionJpaRepository.findPositionByPositionName(any()))
			.thenReturn(new Position("Y"));
		when(contributorJpaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(productContributorJpaRepository.save(any()))
			.thenAnswer(i -> i.getArgument(0));

		Category c5 = new Category( "C5", null);
		when(categoryJpaRepository.findById(5L)).thenReturn(Optional.of(c5));
		when(productCategoryJpaRepository.save(any()))
			.thenAnswer(i -> i.getArgument(0));
		when(productCategoryJpaRepository.findCategoryIdsByProductId(200L))
			.thenReturn(List.of(1L));

		doNothing().when(productSearchService).createProductDocument(any(RequestProductDocumentDTO.class));

		service.createProductByQuery(req);

		verify(productImageJpaRepository).save(any(ProductImage.class));
		verify(productSearchService).createProductDocument(any());
	}

	private static Product createForTest(long productId,
		String title,
		String description,
		LocalDate publishedAt,
		int salePrice) {
		return Product.builder()
			.productId(productId)
			.productState(new ProductState(ProductStateName.SALE))
			.publisher(new Publisher("TEST"))
			.productTitle(title)
			.productDescription(description)
			.productContent("")
			.productIsbn("")
			.productRegularPrice(salePrice)
			.productSalePrice(salePrice)
			.productPackageable(false)
			.productStock(0)
			.productPublishedAt(publishedAt)
			.productImage(new ArrayList<>())
			.build();
	}


}
