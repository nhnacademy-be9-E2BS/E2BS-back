package com.nhnacademy.back.product.like.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.like.domain.dto.request.RequestCreateLikeDTO;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;
import com.nhnacademy.back.product.like.service.LikeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Likes", description = "좋아요 관련 API")
public class LikeRestController {

	private final LikeService likeService;


	@Operation(summary = "좋아요 생성", description = "특정 상품에 대해 사용자가 좋아요를 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "좋아요 생성 성공"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@Member
	@PostMapping("/api/products/{productId}/likes")
	public ResponseEntity<Void> createLike(@Parameter(description = "좋아요할 상품 ID", required = true) @PathVariable long productId,
		                                   @Parameter(description = "좋아요 생성 요청 DTO", required = true) @Validated @RequestBody RequestCreateLikeDTO requestDto, 
		                                   @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		likeService.createLike(productId, requestDto.getMemberId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "좋아요 삭제", description = "특정 상품에 대해 사용자의 좋아요를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "좋아요 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@Member
	@DeleteMapping("/api/products/{productId}/likes")
	public ResponseEntity<Void> deleteLike(@Parameter(description = "좋아요 취소할 상품 ID", required = true) @PathVariable long productId,
		                                   @Parameter(description = "사용자 식별자", required = true) @RequestParam String memberId) {
		likeService.deleteLike(productId, memberId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "회원이 좋아요한 상품 목록 조회", description = "특정 회원이 좋아요한 상품들을 페이징하여 조회합니다.")
	@ApiResponse(responseCode = "200", description = "좋아요 상품 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseLikedProductDTO.class))))
	@Member
	@GetMapping("/api/products/likes")
	public ResponseEntity<Page<ResponseLikedProductDTO>> getLikedProductsByCustomer(@Parameter(description = "회원 아이디", required = true) @RequestParam String memberId,
		                                                                            @Parameter(hidden = true) Pageable pageable) {
		Page<ResponseLikedProductDTO> body = likeService.getLikedProductsByCustomer(memberId, pageable);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "상품 좋아요 수 조회", description = "특정 상품의 좋아요 수를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "좋아요 수 조회 성공", content = @Content(schema = @Schema(implementation = Long.class)))
	@Member
	@GetMapping("/api/products/{productId}/likes/counts")
	public ResponseEntity<Long> getLikeCounts(@Parameter(description = "좋아요 수를 조회할 상품 ID", required = true) @PathVariable long productId) {
		long body = likeService.getLikeCount(productId);
		return ResponseEntity.ok(body);
	}

}