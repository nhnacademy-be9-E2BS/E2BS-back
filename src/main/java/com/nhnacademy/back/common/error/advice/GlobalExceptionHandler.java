package com.nhnacademy.back.common.error.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.exception.AlreadyExistsMemberIdException;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.common.error.dto.GlobalErrorResponse;
import com.nhnacademy.back.common.exception.BadRequestException;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.publisher.exception.PublisherAlreadyExistsException;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 이미 존재하는 경우의 에러 핸들러
	 */
	@ExceptionHandler({CartItemAlreadyExistsException.class, ValidationFailedException.class,
		BadRequestException.class, LoginMemberIsNotExistsException.class, PublisherAlreadyExistsException.class,
		ProductAlreadyExistsException.class, TagAlreadyExistsException.class, ProductStockDecrementException.class})
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
		NotFoundMemberException.class, PublisherNotFoundException.class, WrapperNotFoundException.class, TagNotFoundException.class})
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

}
