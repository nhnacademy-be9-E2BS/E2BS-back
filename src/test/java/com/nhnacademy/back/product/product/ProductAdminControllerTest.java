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
import com.nhnacademy.back.product.category.service.ProductCategoryService;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.kim.controller.ProductAdminController;
import com.nhnacademy.back.product.product.kim.service.ProductService;
import com.nhnacademy.back.product.product.park.service.ProductAPIService;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@WebMvcTest(controllers = ProductAdminController.class)
class ProductAdminControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private ProductService productService;
	@MockitoBean
	private ProductAPIService productAPIService;
	@MockitoBean
	private ProductCategoryService productCategoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("도서 저장")
	void create_product_test() throws Exception {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 100,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		String jsonRequest = objectMapper.writeValueAsString(request);

		when(productService.createProduct(request)).thenReturn(1L);

		// when & then
		mockMvc.perform(post("/api/admin/books")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("전체 도서 페이징 조회")
	void get_products_test() throws Exception {
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

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		when(productService.getProducts(pageable, 0)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/admin/books")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].productTitle").value("title A"))
			.andExpect(jsonPath("$.content[1].productTitle").value("title B"));
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

		List<Long> productIds = List.of(1L, 2L, 3L);
		String jsonRequest = objectMapper.writeValueAsString(productIds);
		when(productService.getProducts(productIds)).thenReturn(dtos);

		// when & then
		mockMvc.perform(get("/api/admin/books/order")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].productTitle").value("title A"))
			.andExpect(jsonPath("$[1].productTitle").value("title B"))
			.andExpect(jsonPath("$[2].productTitle").value("title C"));
	}

	@Test
	@DisplayName("도서 수정")
	void update_product_test() throws Exception {
		// given
		RequestProductDTO request = new RequestProductDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000, 8000, true, 200,
			List.of("a.png", "b.png"), List.of(1L), List.of(1L), List.of(1L));
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/admin/books/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("도서 재고 수정")
	void update_productStock_test() throws Exception {
		// given
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(300);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/admin/books/1/stock")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("도서 판매가 수정")
	void update_productSalePrice_test() throws Exception {
		// given
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(3000);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/admin/books/1/salePrice")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("sale 상태 도서 전체 조회 - coupon 전용")
	void get_products_to_coupon_test() throws Exception {
		// given
		ResponseProductCouponDTO responseA = new ResponseProductCouponDTO(1L, "title A", "name A");
		ResponseProductCouponDTO responseB = new ResponseProductCouponDTO(2L, "title B", "name B");
		ResponseProductCouponDTO responseC = new ResponseProductCouponDTO(3L, "title C", "name C");
		List<ResponseProductCouponDTO> dtos = List.of(responseA, responseB, responseC);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseProductCouponDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		when(productService.getProductsToCoupon(pageable)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/admin/books/status/sale")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].productTitle").value("title A"))
			.andExpect(jsonPath("$.content[1].productTitle").value("title B"))
			.andExpect(jsonPath("$.content[2].productTitle").value("title C"));
	}
}
