package com.nhnacademy.back.common.error.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.back.account.address.exception.DeleteAddressFailedException;
import com.nhnacademy.back.account.address.exception.NotFoundAddressException;
import com.nhnacademy.back.account.address.exception.SaveAddressFailedException;
import com.nhnacademy.back.account.address.exception.UpdateAddressFailedException;
import com.nhnacademy.back.account.customer.exception.CustomerEmailAlreadyExistsException;
import com.nhnacademy.back.account.customer.exception.CustomerEmailNotExistsException;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.exception.AlreadyExistsMemberIdException;
import com.nhnacademy.back.account.member.exception.DeleteMemberFailedException;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.exception.MemberRoleException;
import com.nhnacademy.back.account.member.exception.MemberStateWithdrawException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberStateException;
import com.nhnacademy.back.account.member.exception.UpdateMemberInfoFailedException;
import com.nhnacademy.back.account.member.exception.UpdateMemberRoleFailedException;
import com.nhnacademy.back.account.member.exception.UpdateMemberStateFailedException;
import com.nhnacademy.back.account.oauth.exception.RegisterOAuthFailedException;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.common.error.dto.GlobalErrorResponse;
import com.nhnacademy.back.common.exception.BadRequestException;
import com.nhnacademy.back.common.exception.InvalidImageFormatException;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.exception.OrderDetailNotFoundException;
import com.nhnacademy.back.order.order.exception.OrderNotFoundException;
import com.nhnacademy.back.order.order.exception.OrderProcessException;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.product.category.exception.CategoryAlreadyExistsException;
import com.nhnacademy.back.product.category.exception.CategoryDeleteNotAllowedException;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.exception.ProductCategoryCreateNotAllowException;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotForSaleException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.publisher.exception.PublisherAlreadyExistsException;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;
import com.nhnacademy.back.review.exception.ReviewAlreadyExistsException;
import com.nhnacademy.back.review.exception.ReviewNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 400 BAD REQUEST - 잘못된 요청
	 */
	@ExceptionHandler({
		ValidationFailedException.class,
		IllegalArgumentException.class,
		BadRequestException.class,
		SaveAddressFailedException.class,
		UpdateAddressFailedException.class,
		DeleteAddressFailedException.class,
		LoginMemberIsNotExistsException.class,
		UpdateMemberInfoFailedException.class,
		UpdateMemberStateFailedException.class,
		UpdateMemberRoleFailedException.class,
		DeleteMemberFailedException.class,
		MemberStateWithdrawException.class,
		CustomerEmailNotExistsException.class,
		CustomerEmailAlreadyExistsException.class,
		RegisterOAuthFailedException.class,
		OrderProcessException.class,
		ReviewAlreadyExistsException.class,
		CartItemAlreadyExistsException.class,
		ProductNotForSaleException.class,
		ProductAlreadyExistsException.class,
		ProductStockDecrementException.class,
		PublisherAlreadyExistsException.class,
		TagAlreadyExistsException.class,
		CategoryAlreadyExistsException.class,
		CategoryDeleteNotAllowedException.class,
		ProductCategoryCreateNotAllowException.class
	})
	public ResponseEntity<GlobalErrorResponse> handleBadRequest(Exception ex) {
		return GlobalErrorResponse.buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 404 NOT FOUND - 자원 없음
	 */
	@ExceptionHandler({
		NotFoundAddressException.class,
		NotFoundMemberException.class,
		MemberRoleException.class,
		NotFoundMemberStateException.class,
		CustomerNotFoundException.class,
		ProductNotFoundException.class,
		PublisherNotFoundException.class,
		WrapperNotFoundException.class,
		TagNotFoundException.class,
		CategoryNotFoundException.class,
		OrderNotFoundException.class,
		OrderDetailNotFoundException.class,
		CartNotFoundException.class,
		CartItemNotFoundException.class,
		ReviewNotFoundException.class
	})
	public ResponseEntity<GlobalErrorResponse> handleNotFound(Exception ex) {
		return GlobalErrorResponse.buildErrorResponse(ex, HttpStatus.NOT_FOUND);
	}

	/**
	 * 409 CONFLICT - 리소스 충돌
	 */
	@ExceptionHandler({
		AlreadyExistsMemberIdException.class
	})
	public ResponseEntity<GlobalErrorResponse> handleConflict(Exception ex) {
		return GlobalErrorResponse.buildErrorResponse(ex, HttpStatus.CONFLICT);
	}

	/**
	 * 415 UNSUPPORTED MEDIA TYPE - 지원하지 않는 미디어 타입
	 */
	@ExceptionHandler({
		InvalidImageFormatException.class
	})
	public ResponseEntity<GlobalErrorResponse> handleUnsupportedMediaType(Exception ex) {
		return GlobalErrorResponse.buildErrorResponse(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	/**
	 * 500 INTERNAL SERVER ERROR - 처리되지 않은 기타 예외
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalErrorResponse> handleUnhandledException(Exception ex) {
		return GlobalErrorResponse.buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
