package com.nhnacademy.back.product.product;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.product.product.controller.MainPageProductController;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.service.MainPageProductService;

@WebMvcTest(controllers = MainPageProductController.class)
class MainPageProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MainPageProductService mainPageProductService;

	private ResponseMainPageProductDTO sampleDto(long id, String title) {
		return new ResponseMainPageProductDTO(
			id,
			title,
			"Author",
			"/img.jpg",
			1000L,
			800L,
			"Description",
			"Publisher"
		);
	}

	@Test
	@DisplayName("베스트셀러 가져오기")
	void getBestSeller_returnsOk() throws Exception {
		ResponseMainPageProductDTO dto = sampleDto(1L, "Best Book");
		given(mainPageProductService.getBestSellerProducts())
			.willReturn(List.of(dto));

		mockMvc.perform(get("/api/category/bestseller"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productTitle").value("Best Book"));
	}

	@Test
	@DisplayName("블로그베스트 가져오기")
	void getBlogBest_returnsOk() throws Exception {
		ResponseMainPageProductDTO dto = sampleDto(2L, "Blog Best");
		given(mainPageProductService.getBlogBestProducts())
			.willReturn(List.of(dto));

		mockMvc.perform(get("/api/category/blogbest"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productTitle").value("Blog Best"));
	}

	@Test
	@DisplayName("신간 가져오기")
	void getNewItems_returnsOk() throws Exception {
		ResponseMainPageProductDTO dto = sampleDto(3L, "New Item");
		given(mainPageProductService.getNewItemsProducts())
			.willReturn(List.of(dto));

		mockMvc.perform(get("/api/category/newitems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productTitle").value("New Item"));
	}

	@Test
	@DisplayName("스페셜 가져요기")
	void getNewSpecialItems_returnsOk() throws Exception {
		ResponseMainPageProductDTO dto = sampleDto(4L, "Special Item");
		given(mainPageProductService.getItemNewSpecialProducts())
			.willReturn(List.of(dto));

		mockMvc.perform(get("/api/category/newspecialitems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productTitle").value("Special Item"));
	}

	@Test
	@DisplayName("에디터 초이스 가져오기")
	void getItemEditorChoiceItems_returnsOk() throws Exception {
		ResponseMainPageProductDTO dto = sampleDto(5L, "Editor Item");
		given(mainPageProductService.getItemEditorChoiceProducts())
			.willReturn(List.of(dto));

		mockMvc.perform(get("/api/category/itemeditorchoice"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productTitle").value("Editor Item"));
	}
}
