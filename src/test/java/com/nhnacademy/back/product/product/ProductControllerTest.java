package com.nhnacademy.back.product.product;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.product.controller.ProductController;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.service.ProductService;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("카테고리별 도서 목록 조회 (페이징)")
	void get_products_test() throws Exception {
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

		Pageable pageable = PageRequest.of(0, 9);
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		when(productService.getProducts(pageable, 1L)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/books/category/1")
				.param("page", "0")
				.param("size", "9")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].productTitle").value("title A"))
			.andExpect(jsonPath("$.content[1].productTitle").value("title B"));
	}

	@Test
	@DisplayName("도서 단일 조회")
	void get_product_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryDTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseProductReadDTO response = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryDTO), new ArrayList<>());

		when(productService.getProduct(1L)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/books/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.productIsbn").value("978-89-12345-01-1"));
	}

	@Test
	@DisplayName("productId 리스트를 받아서 도서 리스트 반환 - order 전용")
	void get_products_order_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryADTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseCategoryDTO categoryBDTO = new ResponseCategoryDTO(2L, "category B", null);
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(2L, productStateDTO, publisherDTO, "title B",
			"content B", "description B",
			LocalDate.now(), "978-89-12345-01-2", 9000, 7000, false, 500, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>());
		ResponseProductReadDTO responseC = new ResponseProductReadDTO(3L, productStateDTO, publisherDTO, "title C",
			"content C", "description C",
			LocalDate.now(), "978-89-12345-01-3", 6000, 5000, true, 700, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryBDTO), new ArrayList<>());
		List<ResponseProductReadDTO> dtos = List.of(responseA, responseB, responseC);

		when(productService.getProducts(any())).thenReturn(dtos);

		// when & then
		mockMvc.perform(get("/api/books/order")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("products", "0")
				.param("products", "1")
				.param("products", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].productTitle").value("title A"))
			.andExpect(jsonPath("$[1].productTitle").value("title B"))
			.andExpect(jsonPath("$[2].productTitle").value("title C"));
	}
}
