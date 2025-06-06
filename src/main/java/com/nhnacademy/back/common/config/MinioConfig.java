package com.nhnacademy.back.common.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class MinioConfig {

	@Value("${minio.host}")
	private String host;

	@Value("${minio.access-key}")
	private String accessKey;

	@Value("${minio.secret-key}")
	private String secretKey;

	@Value("${minio.region}")
	private String region;

	/**
	 * minio API 는 AWS Signature v4 방식의 Authorization 헤더를 포함해야한다는데
	 * S3Client 는 AWS Signature Version 4 (SigV4) 인증을 자동으로 처리해준다!
	 *
	 * S3Client 는 MinIO에 요청할 때마다 다음을 자동으로 해준다.
	 * 1.Authorization: AWS4-HMAC-SHA256 ... 헤더 생성
	 * 2.X-Amz-Date, X-Amz-Content-Sha256 등 필요한 헤더 포함
	 * 3.요청 본문에 대한 서명 포함
	 * 4.AWS S3와 완벽히 호환되는 방식으로 MinIO에 요청
	 */
	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.endpointOverride(URI.create(host))
			.credentialsProvider(() -> AwsBasicCredentials.builder()
										.accessKeyId(accessKey)
										.secretAccessKey(secretKey)
										.build())
			.region(Region.of(region))
			.forcePathStyle(true)
			.build();
	}

	/**
	 * 파일 접근을 위한 url 변환 메소드를 사용하기 위한 S3Presigner 설정
	 */
	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
			.endpointOverride(URI.create(host))
			.credentialsProvider(() -> AwsBasicCredentials.builder()
										.accessKeyId(accessKey)
										.secretAccessKey(secretKey)
										.build())
			.region(Region.of(region))
			.serviceConfiguration(S3Configuration.builder()
									.pathStyleAccessEnabled(true) // url 경로로 접근하기 위한 설정
									.build())
			.build();
	}
}
