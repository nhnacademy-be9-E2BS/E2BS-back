package com.nhnacademy.back.elasticsearch;

import java.time.LocalDate;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(indexName = "e2bs-product")
@Setting(settingPath = "elasticsearch/product-setting.json")
@Mapping(mappingPath = "elasticsearch/product-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDocument {

	@Id
	@Field(type = FieldType.Long)
	private Long productId;

	@Field(type = FieldType.Text)
	private String productTitle;

	@Field(type = FieldType.Text)
	private String productContent;

	@Field(type = FieldType.Date, format = DateFormat.basic_date)
	private LocalDate productPublishedAt;

	@Field(type = FieldType.Long)
	private Long productSalePrice;

}
