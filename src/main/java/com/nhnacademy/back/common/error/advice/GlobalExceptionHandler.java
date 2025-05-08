package com.nhnacademy.back.common.error.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.common.error.dto.GlobalErrorResponse;
import com.nhnacademy.back.common.exception.BadRequestException;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 이미 존재하는 경우의 에러 핸들러
	 */
	@ExceptionHandler({CartItemAlreadyExistsException.class, ValidationFailedException.class,
		BadRequestException.class, LoginMemberIsNotExistsException.class})
	public ResponseEntity<?> handleAlreadyExistsException(Exception ex) {
		GlobalErrorResponse body = new GlobalErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	/**
	 * 찾지 못한 경우의 에러 핸들러
	 */
	@ExceptionHandler({CustomerNotFoundException.class, ProductNotFoundException.class, CartItemNotFoundException.class,
		NotFoundMemberException.class})
	public ResponseEntity<?> handleNotFoundException(Exception ex) {
		GlobalErrorResponse body = new GlobalErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}




}
