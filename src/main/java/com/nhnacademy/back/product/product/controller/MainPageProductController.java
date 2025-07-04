package com.nhnacademy.back.product.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.service.MainPageProductService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "도서", description = "메인 페이지 도서 관련 API")
@RestController
@RequestMapping("/api/category")
public class MainPageProductController {
	private final MainPageProductService mainPageProductService;

	public MainPageProductController(MainPageProductService mainPageProductService) {
		this.mainPageProductService = mainPageProductService;
	}


	@GetMapping("/bestseller")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getBestSeller()  {

		List<ResponseMainPageProductDTO> products = mainPageProductService.getBestSellerProducts();
		return ResponseEntity.ok(products);
	}


	@GetMapping("/blogbest")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getBlogBest() {
		List<ResponseMainPageProductDTO> products = mainPageProductService.getBlogBestProducts();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/newitems")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getNewItems() {
		List<ResponseMainPageProductDTO> products = mainPageProductService.getNewItemsProducts();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/newspecialitems")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getNewSpecialItems() {
		List<ResponseMainPageProductDTO> products = mainPageProductService.getItemNewSpecialProducts();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/itemeditorchoice")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getItemEditorChoiceItems() {
		List<ResponseMainPageProductDTO> products = mainPageProductService.getItemEditorChoiceProducts();
		return ResponseEntity.ok(products);
	}






}
