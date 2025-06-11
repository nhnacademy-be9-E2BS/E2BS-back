package com.nhnacademy.back.elasticsearch.lifecycle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;
import com.nhnacademy.back.elasticsearch.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ElasticsearchDataInitializer implements ApplicationRunner {

	private final ElasticsearchOperations operations;
	private final ProductSearchRepository productSearchRepository;

	@Override
	public void run(ApplicationArguments args) {
		IndexOperations indexOps = operations.indexOps(ProductDocument.class);

		if (indexOps.exists()) {
			indexOps.delete();
		}
		indexOps.create(); // 인덱스 생성
		indexOps.putMapping(indexOps.createMapping()); // 매핑 설정

		String aPub = "A pub";
		String bPub = "B pub";
		String cPub = "C pub";
		String aTag = "A tag";
		String bTag = "B tag";
		String cTag = "C tag";

		// 예시 데이터 삽입
		ProductDocument doc1 = new ProductDocument(
			new RequestProductDocumentDTO(
				1L,
				"Spring 입문ㅇㅇ긴 문자인 경우 출력 테스트 ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ",
				"초보자를 위한 Spring 가이드입니다.",
				aPub,
				LocalDate.of(2024, 01, 15),
				15000L,
				List.of("Kim", "Park"),
				List.of(aTag, bTag),
				List.of(1L))
		);

		ProductDocument doc2 = new ProductDocument(
			new RequestProductDocumentDTO(
				2L,
				"JPA 기초",
				"Entity 설계부터 관계 매핑까지 JPA의 핵심을 담았습니다.",
				bPub,
				LocalDate.of(2023, 11, 30),
				14000L,
				List.of("Lee"),
				List.of(aTag),
				List.of(4L)
			)
		);

		ProductDocument doc3 = new ProductDocument(
			new RequestProductDocumentDTO(
				3L,
				"Docker 실전",
				"Docker를 활용한 배포 환경 구성 실전서입니다.",
				aPub,
				LocalDate.of(2022, 6, 10),
				20000L,
				List.of(),
				List.of(cTag),
				List.of(3L)
			)
		);

		ProductDocument doc4 = new ProductDocument(
			new RequestProductDocumentDTO(
				4L,
				"CI/CD 이해하기",
				"CI/CD 파이프라인의 구성과 구현 방법을 설명합니다.",
				bPub,
				LocalDate.of(2025, 3, 1),
				18000L,
				List.of(),
				List.of(cTag),
				List.of(2L, 1L)
			)
		);

		ProductDocument doc5 = new ProductDocument(
			new RequestProductDocumentDTO(
				5L,
				"React 완벽 가이드",
				"React를 사용한 프론트엔드 개발에 대한 완벽 가이드입니다.",
				cPub,
				LocalDate.of(2023, 5, 20),
				18000L,
				List.of("Kim", "Lee"),
				List.of(bTag, cTag),
				List.of(2L, 1L)
			)
		);

		ProductDocument doc6 = new ProductDocument(
			new RequestProductDocumentDTO(
				6L,
				"Vue.js 실전",
				"Vue.js를 사용한 웹 애플리케이션 개발 실전서입니다.",
				aPub,
				LocalDate.of(2023, 9, 10),
				21000L,
				List.of("Park", "Kim"),
				List.of(aTag, bTag),
				List.of(3L, 4L)
			)
		);

		ProductDocument doc7 = new ProductDocument(
			new RequestProductDocumentDTO(
				7L,
				"Machine Learning",
				"머신러닝을 활용한 데이터 분석 및 모델링 실습서를 제공합니다.",
				bPub,
				LocalDate.of(2025, 1, 15),
				20000L,
				List.of("Lee", "Park"),
				List.of(cTag, aTag),
				List.of(5L, 6L)
			)
		);

		ProductDocument doc8 = new ProductDocument(
			new RequestProductDocumentDTO(
				8L,
				"Kubernetes 실전",
				"Kubernetes를 사용한 클러스터 구성 및 배포 전략에 대한 실전 가이드입니다.",
				cPub,
				LocalDate.of(2024, 8, 25),
				22000L,
				List.of("Kim", "Lee"),
				List.of(bTag, aTag),
				List.of(4L, 7L)
			)
		);

		List<ProductDocument> productDocuments = List.of(doc1, doc2, doc3, doc4, doc5, doc6, doc7, doc8);
		productSearchRepository.saveAll(productDocuments);
	}
}

