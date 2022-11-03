package com.aws.springbootlocalstackaws.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.aws.springbootlocalstackaws.model.DownloadedResource;

public interface AwsS3Service {

    List<S3ObjectSummary> getAllDocumentsFromBuckets(String bucketName);

    List<Bucket> getAllBuckets();

    void createBucket(String bucketName);

    void deleteBucketByName(String bucketName);

    void uploadDocument(MultipartFile file, String bucketName) throws IOException;

    void downloadFile(String keyName, String bucketName);

    byte[] downloadFileV2(String keyName, String bucketName);

    DownloadedResource download(String keyName, String bucketName);
}
