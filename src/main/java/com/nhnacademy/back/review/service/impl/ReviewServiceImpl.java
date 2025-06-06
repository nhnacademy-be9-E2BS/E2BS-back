package com.nhnacademy.back.review.service.impl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.event.event.ReviewImgPointEvent;
import com.nhnacademy.back.event.event.ReviewPointEvent;
import com.nhnacademy.back.order.order.domain.entity.OrderDetail;
import com.nhnacademy.back.order.order.exception.OrderDetailNotFoundException;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.review.domain.dto.ReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseMemberReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseUpdateReviewDTO;
import com.nhnacademy.back.review.domain.entity.Review;
import com.nhnacademy.back.review.exception.ReviewNotFoundException;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;
import com.nhnacademy.back.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final CustomerJpaRepository customerRepository;
	private final MemberJpaRepository memberRepository;
	private final ProductJpaRepository productRepository;
	private final OrderDetailJpaRepository orderDetailRepository;
	private final ReviewJpaRepository reviewRepository;

	private final ProductSearchService productSearchService;

	private final MinioUtils minioUtils;
	private static final String REVIEW_BUCKET = "e2bs-reviews-image";
	private static final String PRODUCT_BUCKET = "e2bs-products-image";

	private final ApplicationEventPublisher eventPublisher;

	/**
	 * 리뷰 생성 메소드
	 */
	@Transactional
	@Override
	public void createReview(RequestCreateReviewDTO request) {
		Customer findCustomer = null;
		Member findMember = null;
		MultipartFile reviewImageFile = request.getReviewImage();

		// 회원인 경우
		if (Objects.nonNull(request.getMemberId())) {
			findMember = memberRepository.getMemberByMemberId(request.getMemberId());
			if (Objects.isNull(findMember)) {
				throw new NotFoundMemberException("Member not found");
			}

			findCustomer = customerRepository.findById(findMember.getCustomerId())
				.orElseThrow(CustomerNotFoundException::new);
		}
		// 비회원인 경우
		else if (Objects.nonNull(request.getCustomerId())) {
			findCustomer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(CustomerNotFoundException::new);
		}

		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);

		// 현재 회원이 주문한 상품이면서 (주문 배송이 완료된 상태이면서는 추후에) 리뷰를 아직 작성하지 않았는지 검증
		if (!reviewRepository.existsReviewedOrderDetailsByCustomerIdAndProductId(findCustomer.getCustomerId(), findProduct.getProductId())) {
			throw new OrderDetailNotFoundException();
		}

		// 파일이 존재하면 업로드
		String imagePath = "";
		if (Objects.nonNull(reviewImageFile) && !reviewImageFile.isEmpty()) {
			imagePath = uploadFile(reviewImageFile);
		}

		// 리뷰가 아직 영속성 컨텍스트에 저장 전이므로 다른 필드를 통해 먼저 리뷰가 null 인 주문 상세를 찾아야함
		OrderDetail findOrderDetail = orderDetailRepository.findByCustomerIdAndProductId(findCustomer.getCustomerId(), findProduct.getProductId())
			.orElseThrow(OrderDetailNotFoundException::new);

		// 리뷰 저장
		Review reviewEntity = Review.createReviewEntity(findProduct, findCustomer, request, imagePath);
		reviewRepository.save(reviewEntity);

		// 주문 상세에도 리뷰 필드 갱신
		findOrderDetail.setReview(reviewEntity);
		orderDetailRepository.save(findOrderDetail);

		// 이미지 있으면 이미지 리뷰 정책, 없으면 일반 리뷰 정책으로 포인트 적립 이벤트 발행
		if (Objects.nonNull(request.getMemberId())) {
			if (Objects.nonNull(reviewImageFile) && !reviewImageFile.isEmpty()) {
				eventPublisher.publishEvent(new ReviewImgPointEvent(findMember.getMemberId()));
			} else {
				eventPublisher.publishEvent(new ReviewPointEvent(findMember.getMemberId()));
			}
		}

		// 엘라스틱 서치에서 리뷰 업데이트 (개수, 평균 평점)
		productSearchService.updateProductDocumentReview(request.getProductId(), request.getReviewGrade());
	}

	/**
	 * 파일 업로드 메소드
	 */
	private String uploadFile(MultipartFile reviewImageFile) {
		String originalFilename = reviewImageFile.getOriginalFilename();
		UUID uuid = UUID.randomUUID();
		String objectName = uuid + "_" + originalFilename;
		minioUtils.uploadObject(REVIEW_BUCKET, objectName, reviewImageFile);
		return objectName;
	}

	/**
	 * 리뷰 수정 메소드
	 */
	@Transactional
	@Override
	public ResponseUpdateReviewDTO updateReview(long reviewId, RequestUpdateReviewDTO request) {
		Review findReview = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewNotFoundException::new);

		String updateReviewContent = request.getReviewContent();

		String updateImage = "";
		MultipartFile reviewImageFile = request.getReviewImage();
		if (Objects.nonNull(reviewImageFile) && !reviewImageFile.isEmpty()) {
			// 기존 파일 삭제
			minioUtils.deleteObject(REVIEW_BUCKET, findReview.getReviewImage());

			// 새로 업로드할 파일 등록
			String originalFilename = reviewImageFile.getOriginalFilename();

			UUID uuid = UUID.randomUUID();
			String objectName = uuid + "_" + originalFilename;
			minioUtils.uploadObject(REVIEW_BUCKET, objectName, reviewImageFile);

			// 가공된 파일명 적용
			updateImage = objectName;
		}

		// 변경 감지로 DB의 값 변경
		findReview.changeReview(request, updateImage);

		// 변경 이미지에 대한 url 가공
		String updateImageUrl = minioUtils.getPresignedUrl(REVIEW_BUCKET, updateImage);

		return new ResponseUpdateReviewDTO(updateReviewContent, updateImageUrl);
	}

	/**
	 * 상품 리뷰 페이징 목록 조회 메소드
	 */
	@Override
	public Page<ResponseReviewPageDTO> getReviewsByProduct(long productId, Pageable pageable) {
		Page<Review> getReviewsByProductId = reviewRepository.findAllByProduct_ProductId(productId, pageable);
		return getReviewsByProductId.map(review -> {
			String reviewImagePath = "";
			if (!StringUtils.isEmpty(review.getReviewImage())) {
				reviewImagePath = minioUtils.getPresignedUrl(REVIEW_BUCKET, review.getReviewImage());
			}

			return new ResponseReviewPageDTO(
				review.getReviewId(),
				review.getProduct().getProductId(),
				review.getCustomer().getCustomerId(),
				review.getCustomer().getCustomerName(),
				review.getReviewContent(),
				review.getReviewGrade(),
				reviewImagePath,
				review.getReviewCreatedAt()
			);
		});
	}

	/**
	 * 상품에 대한 리뷰 정보(전체 평점, 각 별의 리뷰 개수) 가져오는 메소드
	 */
	@Override
	public ResponseReviewInfoDTO getReviewInfo(long productId) {
		double totalGradeAvg = reviewRepository.totalAvgReviewsByProductId(productId);
		totalGradeAvg = Math.round(totalGradeAvg * 10) / 10.0;

		int totalCount = reviewRepository.countAllByProduct_ProductId(productId);

		ArrayList<Integer> starCounts = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			int count = reviewRepository.countAllByProduct_ProductIdAndReviewGrade(productId, i);
			starCounts.add(count);
		}

		return new ResponseReviewInfoDTO(totalGradeAvg, totalCount, starCounts);
	}

	/**
	 * 회원이 작성한 리뷰 페이징 목록 조회
	 */
	@Override
	public Page<ResponseMemberReviewDTO> getReviewsByMember(String memberId, Pageable pageable) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		Page<Review> getReviewsByCustomerId = reviewRepository.findAllByCustomer_CustomerId(findMember.getCustomerId(), pageable);

		return getReviewsByCustomerId.map(review -> {
			String productThumbnailImagePath = "";
			if (Objects.nonNull(review.getProduct().getProductImage())) {
				productThumbnailImagePath =  minioUtils.getPresignedUrl(PRODUCT_BUCKET, review.getProduct().getProductImage().getFirst().getProductImagePath());
			}

			String reviewImagePath = "";
			if (!StringUtils.isEmpty(review.getReviewImage())) {
				reviewImagePath = minioUtils.getPresignedUrl(REVIEW_BUCKET, review.getReviewImage());
			}

			return new ResponseMemberReviewDTO(
				review.getReviewId(),
				review.getProduct().getProductId(),
				productThumbnailImagePath,
				review.getProduct().getProductTitle(),
				review.getReviewContent(),
				review.getReviewGrade(),
				reviewImagePath,
				review.getReviewCreatedAt()
			);
		});
	}

	@Override
	public boolean existsReviewedOrderCode(String orderCode) {
		return reviewRepository.existsReviewedOrderCode(orderCode);
	}

	@Override
	public ReviewDTO findByOrderDetailId(long orderDetailId) {
		Review findReview = reviewRepository.findByOrderDetailId(orderDetailId)
			.orElseThrow(ReviewNotFoundException::new);

		return new ReviewDTO(findReview.getProduct().getProductId(), findReview.getCustomer().getCustomerId(), findReview.getReviewId(), findReview.getReviewContent(), findReview.getReviewGrade(), minioUtils.getPresignedUrl(REVIEW_BUCKET, findReview.getReviewImage()));
	}

}
