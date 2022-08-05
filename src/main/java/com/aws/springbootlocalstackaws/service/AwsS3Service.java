package com.aws.springbootlocalstackaws.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;

public interface AwsS3Service {

    List<String> getAllDocumentsFromBuckets(String bucketName);

    List<Bucket> getAllBuckets();

    void createBucket(String bucketName);

    void deleteBucketByName(String bucketName);

    void uploadDocument(MultipartFile file, String bucketName) throws IOException;
}
