package com.nhnacademy.back.product.publisher.domain.dto.response;

import java.util.List;

import com.nhnacademy.back.product.publisher.domain.entity.Publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponsePublisherDTO {
	List<Publisher> publishers;
}
