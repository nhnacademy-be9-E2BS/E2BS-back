package com.nhnacademy.back.cart.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestMergeCartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
import com.nhnacademy.back.cart.service.CartService;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cart", description = "장바구니 관련 API")
public class CartRestController {

	private final CartService cartService;


	@Operation(summary = "비회원 장바구니 병합", description = "비회원의 장바구니를 회원 장바구니로 병합합니다.")
	@ApiResponse(responseCode = "200", description = "장바구니 병합 성공", content = @Content(schema = @Schema(implementation = Integer.class)))
	@PostMapping("/api/carts/merge")
	public ResponseEntity<Integer> mergeCartItemsToMemberFromGuest(@Parameter(description = "병합 요청 DTO", required = true) @RequestBody RequestMergeCartItemDTO requestDto) {
		Integer result = cartService.mergeCartItemsToMemberFromGuest(requestDto.getMemberId(), requestDto.getSessionId());
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "주문한 장바구니 항목 삭제", description = "회원/비회원이 주문한 장바구니를 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "주문한 장바구니 항목 삭제 성공", content = @Content(schema = @Schema(implementation = Integer.class)))
	@PostMapping("/api/carts/orders")
	public ResponseEntity<Integer> deleteOrderCompleteCartItems(@Parameter(description = "주문한 상품 요청 DTO", required = true) @RequestBody RequestDeleteCartOrderDTO requestDto) {
		Integer body = cartService.deleteOrderCompleteCartItems(requestDto);
		return ResponseEntity.ok(body);
	}

	
	/** 회원 장바구니 API **/

	@Operation(summary = "회원 장바구니 생성", description = "회원이 장바구니를 생성합니다.")
	@ApiResponse(responseCode = "201", description = "장바구니 생성 성공", content = @Content(schema = @Schema(implementation = Integer.class)))
	@PostMapping("/api/auth/members/{memberId}/carts")
	public ResponseEntity<Void> createCartByMember(@Parameter(description = "회원 아이디", required = true) @PathVariable String memberId) {
		cartService.createCartForMember(memberId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "회원 장바구니 상품 추가", description = "회원의 장바구니에 상품을 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "장바구니 상품 추가 성공", content = @Content(schema = @Schema(implementation = Integer.class))),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@Member
	@PostMapping("/api/auth/members/carts/items")
	public ResponseEntity<Integer> createCartItemForMember(@Parameter(description = "상품 추가 DTO", required = true) @Valid @RequestBody RequestAddCartItemsDTO requestDto,
		                                                   @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		if (Objects.isNull(requestDto.getMemberId()) && Objects.isNull(requestDto.getSessionId())) {
			throw new IllegalArgumentException("회원아이디와 세션아이디 둘 다 null 일 수 는 없습니다.");
		}

		int cartQuantity = cartService.createCartItemForMember(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cartQuantity);
	}

	@Operation(summary = "회원 장바구니 상품 수정", description = "회원 장바구니 상품 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장바구니 수정 성공", content = @Content(schema = @Schema(implementation = Integer.class))),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@Member
	@PutMapping("/api/auth/members/carts/items/{cartItemId}")
	public ResponseEntity<Integer> updateCartItemForMember(@Parameter(description = "카트 항목 ID", required = true) @PathVariable long cartItemId,
		                                                   @Parameter(description = "수정 요청 DTO", required = true) @Valid @RequestBody RequestUpdateCartItemsDTO requestDto,
		                                                   @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		if (Objects.isNull(requestDto.getMemberId()) && Objects.isNull(requestDto.getSessionId())) {
			throw new IllegalArgumentException("회원아이디와 세션아이디 둘 다 null 일 수 는 없습니다.");
		}

		int cartQuantity = cartService.updateCartItemForMember(cartItemId, requestDto);
		return ResponseEntity.ok(cartQuantity);
	}

	@Operation(summary = "회원 장바구니 상품 삭제", description = "회원 장바구니의 특정 상품을 삭제합니다.")
	@ApiResponse(responseCode = "204", description = "장바구니 항목 삭제 성공")
	@Member
	@DeleteMapping("/api/auth/members/carts/items/{cartItemId}")
	public ResponseEntity<Void> deleteCartItemForMember(@Parameter(description = "카트 항목 ID", required = true) @PathVariable long cartItemId) {
		cartService.deleteCartItemForMember(cartItemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "회원 장바구니 전체 삭제", description = "회원의 장바구니 전체를 삭제합니다.")
	@ApiResponse(responseCode = "204", description = "장바구니 전체 삭제 성공")
	@Member
	@DeleteMapping("/api/auth/members/{memberId}/carts")
	public ResponseEntity<Void> deleteCartForMember(@Parameter(description = "회원 ID", required = true) @PathVariable String memberId) {
		cartService.deleteCartForMember(memberId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "회원 장바구니 조회", description = "회원의 장바구니 상품 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "장바구니 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseCartItemsForMemberDTO.class))))
	@Member
	@GetMapping("/api/auth/members/{memberId}/carts")
	public ResponseEntity<List<ResponseCartItemsForMemberDTO>> getCartItemsByMember(@Parameter(description = "회원 ID", required = true) @PathVariable String memberId) {
		List<ResponseCartItemsForMemberDTO> body = cartService.getCartItemsByMember(memberId);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "회원 장바구니 상품 수 조회", description = "회원 ID로 장바구니에 담긴 상품 수를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "장바구니 상품 수 조회 성공", content = @Content(schema = @Schema(implementation = Integer.class)))
	@Member
	@GetMapping("/api/carts/counts")
	public ResponseEntity<Integer> getCartItemsCountsForMember(@Parameter(description = "회원 ID", required = true) @RequestParam String memberId) {
		Integer result = cartService.getCartItemsCountsForMember(memberId);
		return ResponseEntity.ok(result);
	}


	/** 게스트 장바구니 API **/

	@Operation(summary = "비회원 장바구니 상품 추가", description = "비회원 장바구니에 상품을 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "장바구니 추가 성공", content = @Content(schema = @Schema(implementation = Integer.class))),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@PostMapping("/api/guests/carts/items")
	public ResponseEntity<Integer> createCartItemForGuest(@Parameter(description = "추가 요청 DTO", required = true) @Valid @RequestBody RequestAddCartItemsDTO requestDto,
		                                                  @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		int cartQuantity = cartService.createCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cartQuantity);
	}

	@Operation(summary = "비회원 장바구니 상품 수정", description = "비회원 장바구니 상품 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장바구니 수정 성공", content = @Content(schema = @Schema(implementation = Integer.class))),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@PutMapping("/api/guests/carts/items")
	public ResponseEntity<Integer> updateCartItemForGuest(@Parameter(description = "수정 요청 DTO", required = true) @Valid @RequestBody RequestUpdateCartItemsDTO requestDto,
		                                                  @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		int cartQuantity = cartService.updateCartItemForGuest(requestDto);
		return ResponseEntity.ok(cartQuantity);
	}

	@Operation(summary = "비회원 장바구니 상품 삭제", description = "비회원 장바구니에서 상품을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "장바구니 항목 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@DeleteMapping("/api/guests/carts/items")
	public ResponseEntity<Void> deleteCartItemForGuest(@Parameter(description = "삭제 요청 DTO", required = true) @Valid @RequestBody RequestDeleteCartItemsForGuestDTO requestDto,
		                                               @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.deleteCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "비회원 장바구니 전체 삭제", description = "비회원 장바구니 전체를 삭제합니다.")
	@ApiResponse(responseCode = "204", description = "장바구니 전체 삭제 성공")
	@DeleteMapping("/api/guests/{sessionId}/carts")
	public ResponseEntity<Void> deleteCartForGuest(@Parameter(description = "세션 ID", required = true) @PathVariable String sessionId) {
		cartService.deleteCartForGuest(sessionId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "비회원 장바구니 조회", description = "비회원 장바구니 상품 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "장바구니 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseCartItemsForGuestDTO.class))))
	@GetMapping("/api/guests/{sessionId}/carts")
	public ResponseEntity<List<ResponseCartItemsForGuestDTO>> getCartItemsByGuest(@Parameter(description = "세션 ID", required = true) @PathVariable String sessionId) {
		List<ResponseCartItemsForGuestDTO> body = cartService.getCartItemsByGuest(sessionId);
		return ResponseEntity.ok(body);
	}

}