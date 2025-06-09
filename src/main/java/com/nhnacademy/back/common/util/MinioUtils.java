package com.nhnacademy.back.common.util;

import org.springframework.web.multipart.MultipartFile;

public interface MinioUtils {
	void uploadObject(String bucketName, String objectName, MultipartFile file);
	void deleteObject(String bucketName, String objectName);
	String getPresignedUrl(String bucketName, String objectKey);
}
