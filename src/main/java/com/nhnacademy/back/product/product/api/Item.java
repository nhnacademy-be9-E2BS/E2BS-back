package com.nhnacademy.back.product.product.api;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item{
	private String title = ""; //제목

	private String publisher;//출판사

	private String author; //저자


	private String description; //상품설명

	private String isbn13; //13자리 ISBN

	private int priceStandard; // 정가

	private int priceSales; //판매가

	private LocalDate pubDate;

	private String cover; //상품 이미지
	private String link; // 상품링크

	private String stockstatus; //재고상태


}
