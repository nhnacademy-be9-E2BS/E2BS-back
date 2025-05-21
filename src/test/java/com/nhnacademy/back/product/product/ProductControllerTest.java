package com.nhnacademy.back.product.product;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.kim.controller.ProductAdminController;
import com.nhnacademy.back.product.product.kim.service.ProductService;

@WebMvcTest(controllers = ProductAdminController.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("도서 생성 - 성공")
	void createProductSuccess() throws Exception {
		// given
		RequestProductCreateDTO request = new RequestProductCreateDTO(
			"Test Publisher", "Test Book", "Content", "Description", "1234567890123",
			10000L, 9000L, true, 50, List.of("image1.jpg"), List.of("tag1")
		);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/admin/books")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
		verify(productService, times(1)).createProduct(any(RequestProductCreateDTO.class));
	}

	@Test
	@DisplayName("주문전용 도서 목록 조회 - 성공")
	void getProductsForOrderSuccess() throws Exception {
		// given
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(
			1L, "SALE", "Test Publisher", "Test Book A", "Content", "Description", LocalDate.now(),
			"1234567890123", 10000L, true, 50, List.of("image1.jpg")
		);
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(
			2L, "SALE", "Test Publisher", "Test Book B", "Content", "Description", LocalDate.now(),
			"1234567893352", 100000L, true, 50, List.of("image2.jpg")
		);
		List<ResponseProductReadDTO> products = List.of(responseA, responseB);
		when(productService.getProducts(List.of(1L, 2L))).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/admin/books/order")
				.param("products", "1", "2")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productTitle").value("Test Book A"))
			.andExpect(jsonPath("$[1].productTitle").value("Test Book B"));
		verify(productService, times(1)).getProducts(List.of(1L, 2L));
	}

	@Test
	@DisplayName("도서 수정 - 성공")
	void updateProductSuccess() throws Exception {
		// given
		RequestProductUpdateDTO request = new RequestProductUpdateDTO(
			"SALE", "Test Publisher", "Updated Book", "Updated Content", "Updated Description",
			12000L, 11000L, true, 60, List.of("image2.jpg"), List.of("tag")
		);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/admin/books/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		verify(productService, times(1)).updateProduct(eq(1L), any(RequestProductUpdateDTO.class));
	}

	@Test
	@DisplayName("도서 재고 수정 - 성공")
	void updateProductStockSuccess() throws Exception {
		// given
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(10);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/admin/books/1/stock")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		verify(productService, times(1)).updateProductStock(eq(1L), any(RequestProductStockUpdateDTO.class));
	}

	@Test
	@DisplayName("도서 판매가 수정 - 성공")
	void updateProductSalePriceSuccess() throws Exception {
		// given
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(9500);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/admin/books/1/sale-price")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		verify(productService, times(1)).updateProductSalePrice(eq(1L), any(RequestProductSalePriceUpdateDTO.class));
	}

	@Test
	@DisplayName("쿠폰 적용 가능 도서 조회 - 성공")
	void getProductsForCouponSuccess() throws Exception {
		// given
		ResponseProductCouponDTO response = new ResponseProductCouponDTO(1L, "Test Book", "Test Publisher");
		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseProductCouponDTO> page = new PageImpl<>(List.of(response), pageable, 1);
		when(productService.getProductsToCoupon(pageable)).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/admin/books/status/sale")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].productTitle").value("Test Book"));
		verify(productService, times(1)).getProductsToCoupon(pageable);
	}
}