package com.nhnacademy.back.review.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewMetaDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseMemberReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseUpdateReviewDTO;
import com.nhnacademy.back.review.service.ReviewService;

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
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewRestController {

	private final ReviewService reviewService;


	@Operation(summary = "리뷰 생성", description = "리뷰 내용을 작성하고 이미지를 포함하여 리뷰를 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "리뷰 생성 성공"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@PostMapping(value = "/api/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createReview(@Parameter(description = "리뷰 메타 데이터", required = true) @Validated @RequestPart("requestMeta") RequestCreateReviewMetaDTO requestMeta,
		                                     @Parameter(hidden = true) BindingResult bindingResult,
		                                     @Parameter(description = "리뷰 이미지 파일", required = false) @RequestPart("reviewImage") MultipartFile reviewImage) throws IOException {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		RequestCreateReviewDTO request = new RequestCreateReviewDTO(
			requestMeta.getProductId(),
			requestMeta.getCustomerId(),
			requestMeta.getMemberId(),
			requestMeta.getReviewContent(),
			requestMeta.getReviewGrade(),
			reviewImage
		);
		reviewService.createReview(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "리뷰 수정", description = "리뷰 내용을 수정하고 이미지를 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 수정 성공", content = @Content(schema = @Schema(implementation = ResponseUpdateReviewDTO.class))),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
	})
	@PutMapping("/api/reviews/{reviewId}")
	public ResponseEntity<ResponseUpdateReviewDTO> updateReview(@Parameter(description = "리뷰 ID", required = true) @PathVariable long reviewId,
		                                                        @Parameter(description = "리뷰 내용", required = true) @Validated @RequestPart("reviewContent") String reviewContent,
		                                                        @Parameter(hidden = true) BindingResult bindingResult,
		                                                        @Parameter(description = "수정할 리뷰 이미지", required = false) @RequestPart("reviewImage") MultipartFile reviewImage) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		RequestUpdateReviewDTO request = new RequestUpdateReviewDTO(reviewContent, reviewImage);
		ResponseUpdateReviewDTO body = reviewService.updateReview(reviewId, request);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "상품별 리뷰 목록 조회", description = "특정 상품의 리뷰 목록을 페이징하여 조회합니다.")
	@ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseReviewPageDTO.class))))
	@GetMapping("/api/products/{productId}/reviews")
	public ResponseEntity<Page<ResponseReviewPageDTO>> getReviewsByProduct(@Parameter(description = "상품 ID", required = true) @PathVariable long productId,
		                                                                   @Parameter(hidden = true) Pageable pageable) {
		Page<ResponseReviewPageDTO> body = reviewService.getReviewsByProduct(productId, pageable);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "상품 리뷰 정보 조회", description = "특정 상품의 전체 리뷰 통계 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공", content = @Content(schema = @Schema(implementation = ResponseReviewInfoDTO.class)))
	@GetMapping("/api/products/{productId}/reviews/info")
	public ResponseEntity<ResponseReviewInfoDTO> getReviewInfo(@Parameter(description = "상품 ID", required = true) @PathVariable long productId) {
		ResponseReviewInfoDTO body = reviewService.getReviewInfo(productId);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "회원 리뷰 목록 조회", description = "특정 회원이 작성한 리뷰 목록을 페이징하여 조회합니다.")
	@ApiResponse(responseCode = "200", description = "회원 리뷰 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMemberReviewDTO.class))))
	@GetMapping("/api/members/{memberId}/reviews")
	public ResponseEntity<Page<ResponseMemberReviewDTO>> getReviewsByMember(@Parameter(description = "회원 ID", required = true) @PathVariable String memberId,
		                                                                    @Parameter(hidden = true) Pageable pageable) {
		Page<ResponseMemberReviewDTO> body = reviewService.getReviewsByMember(memberId, pageable);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "주문 상세 리뷰 작성 여부 조회", description = "특정 주문 코드에 대한 리뷰 작성 여부를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "리뷰 작성 여부 조회 성공", content = @Content(schema = @Schema(implementation = Boolean.class)))
	@GetMapping("/api/orders/{orderCode}/reviewed")
	public ResponseEntity<Boolean> isReviewedByOrder(@Parameter(description = "주문 코드", required = true) @PathVariable String orderCode) {
		boolean body = reviewService.existsReviewedOrderCode(orderCode);
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "주문 상세 ID로 리뷰 조회", description = "주문 상세 ID에 해당하는 리뷰 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "리뷰 조회 성공", content = @Content(schema = @Schema(implementation = ResponseReviewDTO.class)))
	@GetMapping("/api/reviews/{orderDetailId}")
	public ResponseEntity<ResponseReviewDTO> findReviewByOrderDetailId(@Parameter(description = "주문 상세 ID", required = true) @PathVariable long orderDetailId) {
		ResponseReviewDTO body = reviewService.findByOrderDetailId(orderDetailId);
		return ResponseEntity.ok(body);
	}
}
