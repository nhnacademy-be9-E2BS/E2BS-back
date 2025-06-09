package com.nhnacademy.back.elasticsearch;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.elasticsearch.controller.ProductSearchController;
import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.service.ProductService;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@WebMvcTest(controllers = ProductSearchController.class)
class ProductSearchControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private ProductService productService;
	@MockitoBean
	private ProductSearchService productSearchService;

	@Test
	@DisplayName("검색어로 도서 검색 후 리스트 조회 (정렬 선택사항)")
	void get_products_by_search_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryADTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(2L, productStateDTO, publisherDTO, "title B",
			"content B", "description B",
			LocalDate.now(), "978-89-12345-01-2", 9000, 7000, false, 500, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		List<ResponseProductReadDTO> dtos = List.of(responseA, responseB);

		Pageable pageable = PageRequest.of(0, 10);
		List<Long> productIdList = List.of(1L, 2L);
		Page<Long> longPage = new PageImpl<>(productIdList, pageable, productIdList.size());
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		String keyword = "title";
		ProductSortType sortType = ProductSortType.NO_SORT;

		when(productSearchService.getProductIdsBySearch(pageable, keyword, sortType)).thenReturn(longPage);
		when(productService.getProductsToElasticSearch(longPage)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/books/elasticsearch/search")
				.param("page", "0")
				.param("size", "10")
				.param("keyword", keyword)
				.param("sort", sortType.toString())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("카테고리 ID에 해당하는 도서 리스트 조회 (정렬 선택사항)")
	void get_products_by_category_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryADTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(2L, productStateDTO, publisherDTO, "title B",
			"content B", "description B",
			LocalDate.now(), "978-89-12345-01-2", 9000, 7000, false, 500, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		List<ResponseProductReadDTO> dtos = List.of(responseA, responseB);

		Pageable pageable = PageRequest.of(0, 10);
		List<Long> productIdList = List.of(1L, 2L);
		Page<Long> longPage = new PageImpl<>(productIdList, pageable, productIdList.size());
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		Long categoryId = 1L;
		ProductSortType sortType = ProductSortType.NO_SORT;

		when(productSearchService.getProductIdsByCategoryId(pageable, categoryId, sortType)).thenReturn(longPage);
		when(productService.getProductsToElasticSearch(longPage)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/books/elasticsearch/category/1")
				.param("sort", sortType.name())
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("메인 페이지 베스트 도서 리스트 조회")
	void get_products_by_main_best_test() throws Exception {
		// given
		ResponseMainPageProductDTO responseA = new ResponseMainPageProductDTO(
			1L, "title A", "contributor A", "https://example.com/image1.jpg",
			15000L, 12000L, "description A", "Pub A");
		ResponseMainPageProductDTO responseB = new ResponseMainPageProductDTO(
			2L, "title B", "contributor B", "https://example.com/image2.jpg",
			20000L, 18000L, "description A", "Pub B");
		List<Long> productIds = List.of(1L, 2L);
		List<ResponseMainPageProductDTO> response = List.of(responseA, responseB);

		when(productSearchService.getBestProductIds()).thenReturn(productIds);
		when(productService.getProductsToMain(productIds)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/books/elasticsearch/main/best")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].productTitle").value("title A"))
			.andExpect(jsonPath("$[1].productTitle").value("title B"));
	}

	@Test
	@DisplayName("메인 페이지 신상 도서 리스트 조회")
	void get_products_by_main_newest_test() throws Exception {
		// given
		ResponseMainPageProductDTO responseA = new ResponseMainPageProductDTO(
			1L, "title A", "contributor A", "https://example.com/image1.jpg",
			15000L, 12000L, "description A", "Pub A");
		ResponseMainPageProductDTO responseB = new ResponseMainPageProductDTO(
			2L, "title B", "contributor B", "https://example.com/image2.jpg",
			20000L, 18000L, "description A", "Pub B");
		List<Long> productIds = List.of(1L, 2L);
		List<ResponseMainPageProductDTO> response = List.of(responseA, responseB);

		when(productSearchService.getNewProductIds()).thenReturn(productIds);
		when(productService.getProductsToMain(productIds)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/books/elasticsearch/main/newest")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].productTitle").value("title A"))
			.andExpect(jsonPath("$[1].productTitle").value("title B"));
	}

	@Test
	@DisplayName("헤더에서 베스트 도서 리스트 조회")
	void get_best_products_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryADTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(2L, productStateDTO, publisherDTO, "title B",
			"content B", "description B",
			LocalDate.now(), "978-89-12345-01-2", 9000, 7000, false, 500, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		List<ResponseProductReadDTO> dtos = List.of(responseA, responseB);

		Pageable pageable = PageRequest.of(0, 10);
		List<Long> productIdList = List.of(1L, 2L);
		Page<Long> longPage = new PageImpl<>(productIdList, pageable, productIdList.size());
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		when(productSearchService.getBestProductIdsHeader(pageable)).thenReturn(longPage);
		when(productService.getProductsToElasticSearch(longPage)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/books/elasticsearch/best")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content[0].productId").value(1L))
			.andExpect(jsonPath("$.content[0].productTitle").value("title A"))
			.andExpect(jsonPath("$.content[1].productId").value(2L))
			.andExpect(jsonPath("$.content[1].productTitle").value("title B"));
	}

	@Test
	@DisplayName("헤더에서 신상 도서 리스트 조회")
	void get_newest_products_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryADTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(2L, productStateDTO, publisherDTO, "title B",
			"content B", "description B",
			LocalDate.now(), "978-89-12345-01-2", 9000, 7000, false, 500, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		List<ResponseProductReadDTO> dtos = List.of(responseA, responseB);

		Pageable pageable = PageRequest.of(0, 10);
		List<Long> productIdList = List.of(1L, 2L);
		Page<Long> longPage = new PageImpl<>(productIdList, pageable, productIdList.size());
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		when(productSearchService.getNewProductIdsHeader(pageable)).thenReturn(longPage);
		when(productService.getProductsToElasticSearch(longPage)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/books/elasticsearch/newest")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content[0].productId").value(1L))
			.andExpect(jsonPath("$.content[0].productTitle").value("title A"))
			.andExpect(jsonPath("$.content[1].productId").value(2L))
			.andExpect(jsonPath("$.content[1].productTitle").value("title B"));
	}
}
