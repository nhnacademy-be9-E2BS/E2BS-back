package com.nhnacademy.back.common.error.advice;

import java.time.LocalDateTime;

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
import com.nhnacademy.back.order.order.exception.PaymentApproveFailedException;
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
	 * HttpStatus.BAD_REQUEST 경우의 에러 핸들러
	 */
	@ExceptionHandler({CartItemAlreadyExistsException.class, ValidationFailedException.class, IllegalArgumentException.class,
		BadRequestException.class, LoginMemberIsNotExistsException.class, PublisherAlreadyExistsException.class,
		ProductAlreadyExistsException.class, TagAlreadyExistsException.class, ProductStockDecrementException.class,
		BadRequestException.class, LoginMemberIsNotExistsException.class, PublisherAlreadyExistsException.class,
		MemberStateWithdrawException.class, CategoryAlreadyExistsException.class,
		CategoryDeleteNotAllowedException.class, ProductCategoryCreateNotAllowException.class,
		SaveAddressFailedException.class, UpdateAddressFailedException.class, DeleteAddressFailedException.class,
		UpdateMemberInfoFailedException.class, UpdateMemberStateFailedException.class,
		UpdateMemberRoleFailedException.class,
		DeleteMemberFailedException.class, SaveAddressFailedException.class, UpdateAddressFailedException.class,
		DeleteAddressFailedException.class, PaymentApproveFailedException.class,
		ReviewAlreadyExistsException.class, RegisterOAuthFailedException.class, CustomerEmailNotExistsException.class,
		CustomerEmailNotExistsException.class, CustomerEmailAlreadyExistsException.class, OrderProcessException.class, ProductNotForSaleException.class})
	public ResponseEntity<GlobalErrorResponse> handleAlreadyExistsException(Exception ex) {
		GlobalErrorResponse body = new GlobalErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(),
			LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	/**
	 * 찾지 못한 경우의 에러 핸들러
	 */
	@ExceptionHandler({CustomerNotFoundException.class, ProductNotFoundException.class,
		CartItemNotFoundException.class, CartNotFoundException.class,
		NotFoundMemberException.class, PublisherNotFoundException.class, WrapperNotFoundException.class,
		ReviewNotFoundException.class, TagNotFoundException.class,
		NotFoundMemberException.class, PublisherNotFoundException.class, WrapperNotFoundException.class,
		ReviewNotFoundException.class, MemberRoleException.class, CategoryNotFoundException.class,
		NotFoundAddressException.class, OrderDetailNotFoundException.class, NotFoundMemberStateException.class,
		OrderNotFoundException.class})
	public ResponseEntity<GlobalErrorResponse> handleNotFoundException(Exception ex) {
		GlobalErrorResponse body = new GlobalErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
			LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	/**
	 * 값의 충돌
	 */
	@ExceptionHandler({AlreadyExistsMemberIdException.class})
	public ResponseEntity<GlobalErrorResponse> handleConflictException(Exception ex) {
		GlobalErrorResponse body = new GlobalErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value(),
			LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	/**
	 * 지원하지 않는 미디어 타입인 경우 에러 핸들러
	 */
	@ExceptionHandler({InvalidImageFormatException.class})
	public ResponseEntity<GlobalErrorResponse> handleUnsupportedMediaTypeException(Exception ex) {
		GlobalErrorResponse body = new GlobalErrorResponse(ex.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
			LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
	}
}
